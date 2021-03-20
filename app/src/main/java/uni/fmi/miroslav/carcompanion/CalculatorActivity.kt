package uni.fmi.miroslav.carcompanion

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

import uni.fmi.miroslav.carcompanion.customelements.ToggleView
import uni.fmi.miroslav.carcompanion.tools.Calc
import uni.fmi.miroslav.carcompanion.tools.Database
import java.sql.SQLException
import java.text.SimpleDateFormat

@SuppressLint("UseSwitchCompatOrMaterialCode")
class CalculatorActivity : AppCompatActivity() {

    private lateinit var unitTV: ToggleView<Button>
    private lateinit var unitET: EditText
    private lateinit var distTV: ToggleView<Button>
    private lateinit var distET: EditText

    private lateinit var saveSwitch: Switch

    private lateinit var toggleTV: ToggleView<TextView>

    private lateinit var mpgBtn: Button
    private lateinit var lkmBtn: Button

    private lateinit var resLastTV: ToggleView<TextView>
    private lateinit var resTV: ToggleView<TextView>
    private lateinit var resAvgTV: ToggleView<TextView>

    private var isMetric: Boolean = true

    //debug
    private val debug: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        //units TV's and ET's
        unitET = findViewById(R.id.fuelCalculatorEditText)
        unitTV = ToggleView(
            findViewById(R.id.unitFuelCalculatorButton),
            getString(R.string.li),
            getString(R.string.gal)
        )
        distET = findViewById(R.id.distanceCalculatorEditText)
        distTV = ToggleView(
            findViewById(R.id.unitDistCalculatorButton),
            getString(R.string.km),
            getString(R.string.mi)
        )

        //save switch
        saveSwitch = findViewById(R.id.saveResultCalculatorSwitch)
        saveSwitch.isChecked = false

        //calculate buttons

        mpgBtn = findViewById(R.id.mpgCalculatorButton)
        lkmBtn = findViewById(R.id.kmCalculatorButton)

        //TV displays
        resTV = ToggleView(
            findViewById(R.id.resultCalculatorTextView)
        )
        resAvgTV = ToggleView(
            findViewById(R.id.avgResultCalculatorTextView)
        )
        resLastTV = ToggleView(
            findViewById(R.id.prevResultCalculatorTextView)
        )

        //setup units on screen and update with Database
        val db = Database(this)
        isMetric = db.getIfMetric(db.readableDatabase)
        unitTV.switchTo(isMetric)
        distTV.switchTo(isMetric)

        //setup and update mpg/lp100km Toggle TV
        toggleTV = ToggleView(
            findViewById(R.id.toggleCalculatorTextView),
            getString(R.string.lp100km),
            getString(R.string.mpg)
        )

        //onClick listeners for units and toggle result unit
        unitTV.setOnClickListener { unitTV.toggle() }
        distTV.setOnClickListener { distTV.toggle() }
        toggleTV.setOnClickListener { switchFields(!toggleTV.isOn) }

        //onClick listeners for mpg and li/100km calculate buttons
        mpgBtn.setOnClickListener { calculate(mpgBtn) }
        lkmBtn.setOnClickListener { calculate(lkmBtn) }


    }

    private fun calculate(btn: Button){

        if (distET.text.isEmpty() || unitET.text.isEmpty()) { return }

        val result : Double
        var km: Double = distET.text.toString().toDouble()
        var li: Double = unitET.text.toString().toDouble()

        if (km <= 0.1 || li <= 0.1) { return }

        //convert to proper metric units :P
        if (!distTV.isOn){
            km = Calc.miToKm(km)
        }
        if (!unitTV.isOn){
            li = Calc.galToLi(li)
        }


        //calculate result
        result = Calc.getLKM(km, li)


        //fill the three text views with info
        resTV.updateInfo(
            formatDouble(result),
            formatDouble(Calc.convert(result))
        )

        val cv = getData()
        val last: Double? = cv?.getAsDouble("last")
        val avg: Double? = cv?.getAsDouble("avg")

        if (last != null){
            resAvgTV.updateInfo(
                    formatDouble(avg!!),
                    formatDouble(Calc.convert(avg))
                )
            resLastTV.updateInfo(
                    formatDouble(last),
                    formatDouble(Calc.convert(last))
                )
        }

        //save result
        if (saveSwitch.isChecked) {
            saveSwitch.isChecked = false
            save(li, km)
        }

        //this syncs the bottom TV with the button for calculation that is pressed
        //also now syncs the other TV's
        if (toggleTV.isOn.xor(btn.id == R.id.kmCalculatorButton)){
            switchFields(btn.id == R.id.kmCalculatorButton)
        }
    }

    private fun formatDouble(num : Double): String{
        return "${((num * 100).toInt() / 100.0)}"
    }

    private fun getData(): ContentValues?{

        var database: SQLiteDatabase? = null
        try {
            val db = Database(this)
            database = db.readableDatabase
            val cv = ContentValues()

            val last = db.getLastFillup(database) ?: return null

            cv.put("last", last)
            cv.put("avg", db.getAverageFillup(database))

            return cv
        } catch (e: SQLException){
            e.printStackTrace()
        } finally {
            database?.close()
        }

        return null
    }

    @SuppressLint("SimpleDateFormat")
    private fun save(fuel: Double, km: Double){
        var db: Database? = null
        var id: Long? = null
        try {
            db = Database(this)
            id = db.insertFillUp(db.writableDatabase, fuel, km, SimpleDateFormat(getString(R.string.SDTF)))

        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db!!.close()
        }

        if (debug) Toast.makeText(this, "result saved under id:$id", Toast.LENGTH_SHORT).show()
    }

    private fun switchFields(boolean: Boolean){
        toggleTV.switchTo(boolean)
        resLastTV.switchTo(boolean)
        resAvgTV.switchTo(boolean)
        resTV.switchTo(boolean)
    }


    override fun onBackPressed() {
        this.finish()
    }




}