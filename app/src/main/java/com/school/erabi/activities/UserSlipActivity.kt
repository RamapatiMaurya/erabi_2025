package com.school.erabi.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.school.erabi.R
import com.school.erabi.db.Student
import com.school.erabi.common.UnicodeFormatter
import com.school.erabi.databinding.ActivityUserslipBinding
import com.school.erabi.db.AppDatabase
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.ENGLISH


class UserSlipActivity : Activity(), Runnable {
    internal lateinit var binding: ActivityUserslipBinding
    internal var mBluetoothAdapter: BluetoothAdapter? = null
    private lateinit var bluetoothManager: BluetoothManager
    private val applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    //  private var mBluetoothConnectProgressDialog: ProgressDialog? = null
    private var mBluetoothSocket: BluetoothSocket? = null
    internal lateinit var mBluetoothDevice: BluetoothDevice

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            binding.progressLayout.visibility = View.GONE
            // mBluetoothConnectProgressDialog!!.dismiss()
            Toast.makeText(this@UserSlipActivity, "DeviceConnected", Toast.LENGTH_SHORT).show()
            binding.mPrint.text = "Print"
        }
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    public override fun onCreate(mSavedInstanceState: Bundle?) {
        super.onCreate(mSavedInstanceState)
        binding = ActivityUserslipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        binding.progressLayout.visibility = View.GONE
        if (mBluetoothAdapter == null) binding.mPrint.text = "Connect"
        binding.edtBillingDate.setText(dateToString_withTime(Date()))

        allListeners()
        allAutoCompleteTextView()

    }// onCreate

    private fun allAutoCompleteTextView() {
        val monthAdapter = ArrayAdapter(this@UserSlipActivity,
            android.R.layout.select_dialog_item, getResources().getStringArray(R.array.month))
        binding.txtMonth.threshold = 1
        binding.txtMonth.setAdapter(monthAdapter)

        val yearAdapter = ArrayAdapter(this@UserSlipActivity,
            android.R.layout.select_dialog_item, getResources().getStringArray(R.array.year))
        binding.txtYear.threshold = 1
        binding.txtYear.setAdapter(yearAdapter)
    }

    private fun allListeners(){
        binding.Scan.setOnClickListener { scan() }

        binding.mPrint.setOnClickListener {
            if (mBluetoothAdapter == null) scan()
            else {
                val studentData =  validateStudent()
                studentData?.let {
                     val mydb = AppDatabase.getInstance(applicationContext)
                    val billNo = getBillNo(it)
                    studentData.billNo = billNo
                     mydb?.studentDao()?.insertStudent(studentData)
                    printBill(it)
                    val intent = Intent(this, SuccessActivity::class.java)
                    intent.putExtra("billNo", it.billNo)
                     startActivity(intent)
                     finish()

                }
            }
        }

        binding.mDisc.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@setOnClickListener
            }
            if (mBluetoothAdapter != null)
                mBluetoothAdapter!!.disable()
        }

        binding.edtExamFee.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    val st = "${ s.toString().toInt() + getIntValue(binding.edtOtherFee.text) +
                            getIntValue(binding.edtVanFee.text) + getIntValue(binding.edtTutionFee.text)
                    }"
                    binding.txtTotal.setText(st)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        binding.edtVanFee.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    val st = "${s.toString().toInt() + getIntValue(binding.edtExamFee.text) +
                            getIntValue(binding.edtTutionFee.text) + getIntValue(binding.edtOtherFee.text)
                    }"
                    binding.txtTotal.setText(st)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        binding.edtTutionFee.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    val st = "${ s.toString().toInt() + getIntValue(binding.edtExamFee.text) +
                            getIntValue(binding.edtVanFee.text) + getIntValue(binding.edtOtherFee.text)
                    }"
                    binding.txtTotal.setText(st)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })

        binding.edtOtherFee.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    val st = "${s.toString().toInt() + getIntValue(binding.edtExamFee.text) +
                            getIntValue(binding.edtVanFee.text) + getIntValue(binding.edtTutionFee.text)
                    }"
                    binding.txtTotal.setText(st)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        binding.lblBillingDate.setOnClickListener {
            val fromCal = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this@UserSlipActivity,
                { view12: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->

                    val day = String.format(ENGLISH, "%02d", selectedDay)
                    val month = String.format(ENGLISH, "%02d", selectedMonth+1)

                    binding.edtBillingDate.setText("$day-$month-$selectedYear")


                }, fromCal[1], fromCal[2], fromCal[5]
            )
            datePickerDialog.setCancelable(false)
            datePickerDialog.setTitle("Select the date")
            datePickerDialog.show()
        }
    }

    private fun getBillNo(student: Student): String {
        val date = Date()
        val sb = StringBuilder()
        return sb.append(getYear(date)?.substring(2))
            .append(getMonth(date)!!.padStart(2, '0'))
            .append(student.className.padStart(2, '0'))
                .append(student.rollNo.padStart(3, '0')).toString()
    }

    private fun validateStudent(): Student? {

        when {
            binding.edtStudentName.text!!.isEmpty() -> callToast(binding.edtStudentName, "Please enter Student Name")
            binding.edtFatherName.text!!.isEmpty() -> callToast(binding.edtFatherName, "Please enter Father Name")
            binding.edtClassName.text!!.isEmpty() -> callToast(binding.edtClassName, "Please enter Class Name")
            binding.edtRollNo.text!!.isEmpty() -> callToast(binding.edtRollNo, "Please enter Roll No.")
            binding.txtMonth.text!!.isEmpty() -> callToast(binding.txtMonth, "Please enter Month Name")
            binding.txtYear.text!!.isEmpty() || (binding.txtYear.text!!.substring(0,2) !="20")   -> callToast(binding.txtYear, "Please enter Year")
            binding.edtTutionFee.text!!.isEmpty() -> callToast(binding.edtTutionFee, "Please enter Tution fees")
            binding.edtVanFee.text!!.isEmpty() -> callToast(binding.edtVanFee, "Please enter Van fees")
            binding.edtExamFee.text!!.isEmpty() -> callToast(binding.edtExamFee, "Please enter Exam fees")
            binding.edtOtherFee.text!!.isEmpty() -> callToast(binding.edtOtherFee, "Please enter Other fees")
            binding.txtTotal.text!!.isEmpty() || binding.txtTotal.text!!.equals("0") ->
                callToast(null, "Please enter fee details")

            else -> {
                return Student(
                    null, null,
                    binding.lblSchoolName.text.toString(),
                    binding.edtStudentName.text.toString(),
                    binding.edtFatherName.text.toString(),
                    binding.edtClassName.text.toString(),
                    binding.edtRollNo.text.toString(),
                    binding.txtMonth.text.toString(),
                    binding.txtYear.text.toString(),
                    binding.edtBillingDate.text.toString(),
                    binding.edtTutionFee.text.toString().toDouble(),
                    binding.edtVanFee.text.toString().toDouble(),
                    binding.edtExamFee.text.toString().toDouble(),
                    binding.edtOtherFee.text.toString().toDouble(),
                    binding.txtTotal.text.toString().toDouble(),
                    binding.lblCellNo.text.toString()
                )
            }
        }

        return null

    }

    private fun callToast(view: View?, msg: String){
        view?.requestFocus()
        Toast.makeText(this@UserSlipActivity, msg, Toast.LENGTH_LONG).show()
    }

/*    private fun validateField(editText : EditText, ) : String {
        if(editText.text.isEmpty()) {
            Toast.makeText(this@MainActivity, "$editText field can't be blank", Toast.LENGTH_SHORT)
                .show()
            editText.requestFocus()
           break;
        } else {
           return editText.text.toString()
        }
    }*/

    @SuppressLint("MissingPermission")
    fun scan() {
        //    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null) {
            Toast.makeText(this@UserSlipActivity, "Message1", Toast.LENGTH_SHORT).show()
        } else {
            if (!mBluetoothAdapter!!.isEnabled) {
                if(checkPermission(this@UserSlipActivity)) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                }
            } else {
                listPairedDevices()
                val connectIntent = Intent(this@UserSlipActivity, DeviceListActivity::class.java)
                startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE)
            }
        }
    }

    public override fun onActivityResult(mRequestCode: Int, mResultCode: Int, mDataIntent: Intent?) {
        if(mResultCode != RESULT_CANCELED) {
            if (checkPermission(this@UserSlipActivity))
                when (mRequestCode) {
                    REQUEST_CONNECT_DEVICE -> if (mResultCode == RESULT_OK) {
                        if (mDataIntent != null) {
                            val mExtra = mDataIntent.extras
                            val mDeviceAddress = mExtra!!.getString("DeviceAddress")
                            Log.v(TAG, "Coming incoming address " + mDeviceAddress!!)
                            mBluetoothDevice = mBluetoothAdapter!!.getRemoteDevice(mDeviceAddress)
                            binding.progressLayout.visibility = View.VISIBLE
                            val s = "${mBluetoothDevice.name} + : + ${mBluetoothDevice.address}"
                            binding.progressText.text = s
                            binding.progressCircular.isIndeterminate = true
                            /*  mBluetoothConnectProgressDialog = ProgressDialog.show(
                          this,
                          "Connecting...", mBluetoothDevice.name + " : "
                                  + mBluetoothDevice.address, true, false
                      )*/
                            val mBlutoothConnectThread = Thread(this)
                            mBlutoothConnectThread.start()
                            // pairToDevice(mBluetoothDevice); This method is replaced by
                            // progress dialog with thread
                        }
                        Log.v(TAG, "mDataIntent:  $mDataIntent")
                    }

                    REQUEST_ENABLE_BT -> if (mResultCode == RESULT_OK) {
                        listPairedDevices()
                        val connectIntent = Intent(
                            this@UserSlipActivity,
                            DeviceListActivity::class.java
                        )
                        startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE)
                    } else {
                        Toast.makeText(this@UserSlipActivity, "Message", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun listPairedDevices() {
        if (checkPermission(this@UserSlipActivity)) {
            val mPairedDevices = mBluetoothAdapter!!.bondedDevices
            if (mPairedDevices.size > 0) {
                for (mDevice in mPairedDevices) {
                    Log.v(
                        "TAG", "PairedDevices: " + mDevice.name + "  "
                                + mDevice.address
                    )
                }
            }
        }
    }

    private fun printBill(student: Student) {
        val t = object : Thread() {
            override fun run() {
                try {
                    val os = mBluetoothSocket!!.outputStream

                    var bill = ("\n${student.schoolName}\n" +
                            "${getString(R.string.bill_number)}  : ${student.billNo}\n" +
                            "${getString(R.string.student_name)} : ${student.studentName}\n" +
                            "${getString(R.string.father_name)}  :  ${student.fatherName}\n" +
                            "${getString(R.string.class_name)}   : ${student.className}\n" +
                            "${getString(R.string.roll_no)}     : ${student.rollNo}\n" +
                            "${getString(R.string.month_name)}   : ${student.month}/${student.year}\n" +
                            "${getString(R.string.billing_date)} : ${student.billingDate}\n" +
                            "${getString(R.string.tution_fees)}: ${student.tutionFees}\n" +
                            "${getString(R.string.van_fees)}   : ${student.vanFees}\n" +
                            "${getString(R.string.exam_fees)}  : ${student.examFee}\n" +
                            "${getString(R.string.other_fees)} : ${student.otherFees}\n" +

                            "--------------------------------\n" +
                            "${getString(R.string.total)} : ${student.total}\n" +

                            "--------------------------------\n" +
                            "${student.msg}\n" +
                            "--------------------------------\n\n")

//                        BILL = BILL + "\n " + String.format(
//                            "%1$-10s %2$10s %3$11s %4$10s",
//                            "item-002",
//                            "10",
//                            "5",
//                            "50.00"
//                        )
//                        BILL = BILL + "\n " + String.format(
//                            "%1$-10s %2$10s %3$11s %4$10s",
//                            "item-003",
//                            "20",
//                            "10",
//                            "200.00"
//                        )


                    bill = bill + "\n"
                    os.write(bill.toByteArray())
                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    val gs = 35
                    os.write(intToByteArray(gs).toInt())
                    val h = 104
                    os.write(intToByteArray(h).toInt())
                    val n = 162
                    os.write(intToByteArray(n).toInt())

                    // Setting Width
                    val gs_width = 39
                    os.write(intToByteArray(gs_width).toInt())
                    val w = 119
                    os.write(intToByteArray(w).toInt())
                    val n_width = 2
                    os.write(intToByteArray(n_width).toInt())


                } catch (e: Exception) {
                    Log.e("MainActivity", "Exe ", e)
                }

            }
        }
        t.start()
    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket!!.close()
        } catch (e: Exception) {
            Log.e("Tag", "Exe ", e)
        }

    }


    override fun onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket!!.close()
        } catch (e: Exception) {
            Log.e("Tag", "Exe ", e)
        }

        setResult(RESULT_CANCELED)
        finish()
    }

    override fun run() {
        try {
            if (checkPermission(this@UserSlipActivity))
                mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID)
            mBluetoothAdapter!!.cancelDiscovery()
            mBluetoothSocket!!.connect()
            mHandler.sendEmptyMessage(0)
        } catch (eConnectException: IOException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException)
            closeSocket(mBluetoothSocket!!)
            return
        }

    }

    private fun closeSocket(nOpenSocket: BluetoothSocket) {
        try {
            nOpenSocket.close()
            Log.d(TAG, "SocketClosed")
        } catch (ex: IOException) {
            Log.d(TAG, "CouldNotCloseSocket")
        }

    }

    fun sel(data: Int): ByteArray {
        val buffer = ByteBuffer.allocate(2)
        buffer.putInt(data)
        buffer.flip()
        return buffer.array()
    }

    companion object {
        protected const val TAG = "TAG"
        private const val REQUEST_CONNECT_DEVICE = 1
        private const val REQUEST_ENABLE_BT = 2

        fun intToByteArray(value: Int): Byte {
            val b = ByteBuffer.allocate(4).putInt(value).array()

            for (k in b.indices) {
                println(
                    "Selva  [" + k + "] = " + "0x"
                            + UnicodeFormatter.byteToHex(b[k])
                )
            }

            return b[3]
        }

        fun getIntValue(view : Editable?) : Double {
            if(view.toString().isEmpty())
                return 0.0
            else
                return view.toString().toDouble()
        }

        fun createStringDate(day: Int, month: Int, year: Int): String? {
            return String.format(ENGLISH, "%02d", day) + "/" + String.format(ENGLISH, "%02d", month) + "/" + year
        }

        fun dateToString(date: Date): String? {
            return try {
                SimpleDateFormat("dd/MM/yyyy", ENGLISH).format(date)
            } catch (pe: java.lang.Exception) {
                pe.printStackTrace()
                null
            }
        }

        fun getYear(date: Date): String? {
            return try {
                SimpleDateFormat("yyyy", ENGLISH).format(date)
            } catch (pe: java.lang.Exception) {
                pe.printStackTrace()
                null
            }
        }

        fun getMonth(date: Date): String? {
            return try {
                SimpleDateFormat("MM", ENGLISH).format(date)
            } catch (pe: java.lang.Exception) {
                pe.printStackTrace()
                null
            }
        }

        fun dateToString_withTime(date: Date): String? {
            return try {
                SimpleDateFormat("dd/MM/yyyy hh:mm:ss", ENGLISH).format(date)
            } catch (pe: java.lang.Exception) {
                pe.printStackTrace()
                null
            }
        }

        fun checkPermission(activity: Activity): Boolean {
            var b = true

            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_ADMIN
                ) != PackageManager.PERMISSION_GRANTED
                //  || ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                //  || ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                b = false
                requestStoragePermission(activity)
            }

            return b
        }

        private fun requestStoragePermission(activity: Activity) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH_ADMIN)
//                || ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.BLUETOOTH_SCAN)
//                || ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.BLUETOOTH_CONNECT)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                AlertDialog.Builder(activity)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", { dialogInterface: DialogInterface, which: Int ->
                        ActivityCompat.requestPermissions(
                            activity, arrayOf(
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
//                                Manifest.permission.BLUETOOTH_SCAN,
//                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ), 101
                        )
                    })
                    .setNegativeButton("cancel") { dialog: DialogInterface, which: Int ->
                        dialog.dismiss()
                    }
                    .create().show()
            } else {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
//                        Manifest.permission.BLUETOOTH_SCAN,
//                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 101
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == 101) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }

}