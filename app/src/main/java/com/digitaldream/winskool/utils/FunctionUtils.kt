package com.digitaldream.winskool.utils

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.text.SpannableString
import android.text.style.SuperscriptSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.AccountSetupDataModel
import com.digitaldream.winskool.models.ChartModel
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.GroupItem
import com.digitaldream.winskool.models.LevelTable
import com.digitaldream.winskool.models.VendorModel
import org.achartengine.ChartFactory
import org.achartengine.GraphicalView
import org.achartengine.chart.PointStyle
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object FunctionUtils {
    //var counter = 0
    @JvmStatic
    fun capitaliseFirstLetter(sentence: String): String {
        val letters = sentence.lowercase(Locale.getDefault()).split(" ".toRegex()).toTypedArray()
        val stringBuilder = StringBuilder()

        letters.forEach {
            try {
                val words =
                    it.substring(0, 1).uppercase(Locale.getDefault()) +
                            it.substring(1).lowercase(Locale.getDefault())
                stringBuilder.append(words).append(" ")
            } catch (sE: Exception) {
                sE.printStackTrace()
            }
        }

        return stringBuilder.toString()
    }


    fun setColor(): Int {
        val random = Random()
        return Color.argb(
            255, random.nextInt(206), random.nextInt(256),
            random.nextInt(256)
        )
    }


    /*    public static CountDownTimer startCountDown(ProgressBar sProgressBar,
                                                int sI,
                                      TextView sTextView) {

        return new CountDownTimer(5 * 1000, 1) {
            @Override
            public void onTick(long sL) {

                counter += 1;
                if (counter < sI + 1) {
                    sProgressBar.setProgress(counter);
                    sTextView.setText(counter + "%");
                    sProgressBar.setMax(100);
                }

            }

            @Override
            public void onFinish() {

            }
        };

    }*/


    @JvmStatic
    fun animateObject(sProgressBar: ProgressBar, sTextView: TextView, sI: Int) {
        ObjectAnimator.ofInt(sProgressBar, "progress", sI)
            .setDuration(1000)
            .start()
        val animator = ValueAnimator.ofInt(0, sI)
        animator.duration = 1000
        animator.addUpdateListener {
            sTextView.text = String.format("%s%%", animator.animatedValue)
        }
        animator.start()
    }


    @JvmStatic
    fun plotLineChart(
        sValues: MutableList<ChartModel>,
        sContext: Context,
        sVerticalTitle: String?,
        sHorizontalTitle: String?,
    ): GraphicalView {

        val graphLength = sValues.size

        val series = XYSeries(sVerticalTitle)

        for (i in 0 until graphLength)
            series.add(i.toDouble(), sValues[i].value.toDouble())

        val dataset = XYMultipleSeriesDataset()
        dataset.addSeries(series)

        val seriesRenderer = XYSeriesRenderer().apply {
            color = ContextCompat.getColor(sContext, R.color.color_4)
            isFillPoints = true
            annotationsColor = Color.WHITE
            lineWidth = 4f
            pointStyle = PointStyle.CIRCLE
            isDisplayChartValues = true
            chartValuesTextSize = 20f
        }


        val multipleRenderer = XYMultipleSeriesRenderer().apply {
            xLabels = 0
            yLabels = 0
            xTitle = sHorizontalTitle
            isPanEnabled = false
            marginsColor = Color.parseColor("#191F91")
            isZoomEnabled = false
            backgroundColor = Color.parseColor("#191F91")
            isApplyBackgroundColor = true
            labelsColor = Color.WHITE
            labelsTextSize = 20f
            addSeriesRenderer(seriesRenderer)
            margins = intArrayOf(20, 30, 15, 0)


            for (i in 0 until graphLength)
                addXTextLabel(i.toDouble(), sValues[i].label)
        }


        return ChartFactory.getLineChartView(
            sContext, dataset, multipleRenderer,
        )
    }


    @JvmStatic
    fun currencyFormat(number: Double): String {
        val formatter = DecimalFormat("###,###,##0.00")
        return formatter.format(number)
    }


    @JvmStatic
    fun formatDate(date: String): String {
        var formattedDate = ""
        try {

            val simpleDateFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            )

            val parseDate = simpleDateFormat.parse("$date 00:00:00")!!
            val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            formattedDate = sdf.format(parseDate)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return formattedDate
    }

    @JvmStatic
    fun formatDate2(date: String, format: String = "default"): String {
        var formattedDate = ""
        try {
            val simpleDateFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            )

            val parseDate = if (date.length <= 10) {
                simpleDateFormat.parse("$date 00:00:00")!!
            } else {
                simpleDateFormat.parse(date)!!
            }

            val sdf = when (format) {
                "default" -> SimpleDateFormat("dd, MMM", Locale.getDefault())
                "custom" -> SimpleDateFormat("dd MMM", Locale.getDefault())
                "full" -> SimpleDateFormat("EEE, dd MMM, yyyy", Locale.getDefault())
                else -> SimpleDateFormat("EEE, dd MMM HH:mm", Locale.getDefault())
            }
            formattedDate = sdf.format(parseDate)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return formattedDate
    }

    @JvmStatic
    fun getEndDate(startDate: String): String {
        var formattedDate = ""
        try {
            val calender = Calendar.getInstance()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parseDate = simpleDateFormat.parse(startDate)
            calender.time = parseDate!!
            calender.add(Calendar.MONTH, 1)
            formattedDate = simpleDateFormat.format(calender.time)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return formattedDate

    }

    @JvmStatic
    fun getDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR].toString()
        val month = (calendar[Calendar.MONTH] + 1).toString()
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH].toString()
        return "$year-$month-$dayOfMonth"
    }


    private fun createBitMap(sView: View, sWidth: Int, sHeight: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(sWidth, sHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        sView.draw(canvas)
        return bitmap
    }


    private fun createPDF(sView: View, sActivity: Activity): PdfDocument {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        sActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels //1.4142
        val height = displayMetrics.heightPixels

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var bitmap = createBitMap(sView, sView.width, sView.height)
        bitmap = Bitmap.createScaledBitmap(bitmap, sView.width, sView.height, true)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        pdfDocument.finishPage(page)

        return pdfDocument
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun notification(max: Int, sFile: File, sActivity: Activity) {
        val notificationManger: NotificationManagerCompat =
            NotificationManagerCompat.from(sActivity)

        if (ActivityCompat.checkSelfPermission(
                sActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {

            val pendingIntent = PendingIntent.getActivity(
                sActivity,
                0, notificationIntent(sFile, sActivity), PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(
                sActivity, CHANNEL_ID
            ).apply {
                setSmallIcon(R.drawable.ddl_logo)
                setContentTitle("Payment Receipt")
                setContentText("Download in progress")
                setContentIntent(pendingIntent)
                color = ContextCompat.getColor(sActivity, R.color.color_5)
                setProgress(max, 0, false)
                setOngoing(true)
                setOnlyAlertOnce(true)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }

            notificationManger.notify(1, notification.build())

            Thread {
                SystemClock.sleep(1000)
                for (progress in 0..max step 10) {
                    notification.setProgress(max, progress, false)
                    notificationManger.notify(1, notification.build())
                    SystemClock.sleep(1000)
                }
                notification.setContentText("Download finished")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                notificationManger.notify(1, notification.build())
            }.start()
        }


    }


    private fun notificationIntent(sFile: File, sActivity: Activity): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            sActivity, sActivity.packageName + "" +
                    ".provider", sFile
        )
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return intent
    }


    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun downloadPDF(sView: View, sActivity: Activity) {

        val randomId = UUID.randomUUID().toString()
        try {
            val file = File(
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .absolutePath + "/receipt$randomId.pdf"
            )

            createPDF(sView, sActivity).writeTo(FileOutputStream(file))
            val fileSize = (file.length() / 1024).toInt()
            notification(fileSize, file, sActivity)

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                sActivity, "Something went wrong please try again!", Toast
                    .LENGTH_SHORT
            ).show()
        }
        createPDF(sView, sActivity).close()
    }


    @JvmStatic
    fun sharePDF(sView: View, sActivity: Activity) {

        val path = sActivity.cacheDir
        val output = File.createTempFile("receipt", ".pdf", path)

        try {
            createPDF(sView, sActivity).writeTo(FileOutputStream(output))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        createPDF(sView, sActivity).close()

        val uri = FileProvider.getUriForFile(
            sActivity,
            "${sActivity.packageName}.provider",
            output
        )

        ShareCompat.IntentBuilder(sActivity).apply {
            setType("application/pdf")
            setSubject("Share Pdf")
            addStream(uri)
            setChooserTitle("Share receipt")
            startChooser()
        }
    }


    @JvmStatic
    fun sendRequestToServer(
        method: Int,
        url: String,
        context: Context,
        stringHashMap: HashMap<String, String>?,
        volleyCallback: VolleyCallback,
        isShowProgressBar: Boolean = true
    ) {
        val sharedPreferences = context.getSharedPreferences("loginDetail", MODE_PRIVATE)
        val db = sharedPreferences.getString("db", "")

        val progressFlower = ACProgressFlower.Builder(context).apply {
            direction(ACProgressConstant.DIRECT_CLOCKWISE)
            textMarginTop(10)
            fadeColor(ContextCompat.getColor((context as AppCompatActivity), R.color.color_5))
        }.build()

        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)

        if (isShowProgressBar) {
            progressFlower.show()
        }

        val mUrl = if (stringHashMap.isNullOrEmpty()) "$url&&_db=$db" else url

        val stringRequest = object : StringRequest(
            method,
            mUrl,
            { response: String ->
                Timber.tag("response").d(response)
                volleyCallback.onResponse(response)

                if (isShowProgressBar) {
                    progressFlower.dismiss()
                }


            }, { error: VolleyError ->
                volleyCallback.onError(error)
                Timber.tag("error").e(error)

                if (isShowProgressBar) {
                    progressFlower.dismiss()
                }

            }) {

            override fun getParams(): MutableMap<String, String> {
                val stringMap = mutableMapOf<String, String>()

                if (!stringHashMap.isNullOrEmpty()) {
                    for ((key, value) in stringHashMap) {
                        stringMap[key] = value
                    }
                    stringMap["_db"] = db ?: ""
                }

                return stringMap
            }

        }

        Volley.newRequestQueue(context).add(stringRequest)
    }


    @JvmStatic
    fun webViewProgress(context: Context, webView: WebView) {
        val progressFlower = ACProgressFlower.Builder(context)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(5)
            .fadeColor(ContextCompat.getColor((context as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100)
                    progressFlower.dismiss()
            }
        }
    }


    @JvmStatic
    fun selectDeselectButton(button: Button, type: String) {

        if (type == "deselected") {
            button.apply {
                isSelected = false
                setBackgroundResource(R.drawable.ripple_effect6)
                setTextColor(Color.BLACK)
            }
        } else {
            button.apply {
                isSelected = true
                setBackgroundResource(R.drawable.ripple_effect7)
                setTextColor(Color.WHITE)
            }
        }
    }


    @JvmStatic
    fun getRandomColor(view: View) {
        val mutate = view.background.mutate() as GradientDrawable
        val hue = Random().nextInt(256)
        val color = Color.HSVToColor(floatArrayOf(hue.toFloat(), 2f, 5f))
        val colorStateList = ColorStateList.valueOf(color)
        mutate.color = colorStateList
        view.background = mutate
    }


    @JvmStatic
    fun flipAnimation(
        context: Context,
        frontView: View,
        backView: View,
        direction: String
    ) {
        val cameraDistance = context.resources.displayMetrics.density * 8000
        frontView.cameraDistance = cameraDistance
        backView.cameraDistance = cameraDistance

        val flipRightAnimator =
            AnimatorInflater.loadAnimator(context, R.animator.out_animator) as AnimatorSet
        val flipLeftAnimator =
            AnimatorInflater.loadAnimator(context, R.animator.in_animator) as AnimatorSet

        when (direction) {
            "right" -> {
                flipRightAnimator.run {
                    setTarget(frontView)
                    flipLeftAnimator.setTarget(backView)
                    start()
                    flipLeftAnimator.start()

                    doOnEnd {
                        frontView.isVisible = false
                        backView.isVisible = true
                    }
                }
            }

            else -> {
                flipLeftAnimator.run {
                    setTarget(frontView)
                    flipRightAnimator.setTarget(backView)
                    flipRightAnimator.start()
                    start()

                    doOnEnd {
                        backView.isVisible = false
                        frontView.isVisible = true
                    }

                }
            }
        }
    }


    @JvmStatic
    fun getSelectedItem(selectedItem: HashMap<String, String>, from: String): String {

        return JSONObject().apply {
            val jsonArray = JSONArray()
            selectedItem.forEach { (key, value) ->
                if (key.isNotEmpty() && value.isNotEmpty()) {
                    JSONObject().apply {
                        put("id", key)
                        put("name", value)
                    }.let {
                        jsonArray.put(it)
                    }
                }
            }

            when (from) {
                "level" -> put(from, jsonArray)
                "class" -> put(from, jsonArray)
                "cid" -> put(from, jsonArray)
                "account" -> put(from, jsonArray)
            }

        }.toString()

    }


    @JvmStatic
    fun parseFilterJson(json: String, from: String): String {
        val nameList = mutableListOf<String>()

        try {
            JSONObject(json).run {
                val jsonArray = getJSONArray(from)
                for (i in 0 until jsonArray.length()) {
                    val name = jsonArray.getJSONObject(i).getString("name")
                    nameList.add(name)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return when (nameList.size) {
            0 -> ""
            1 -> nameList[0]
            else -> nameList.dropLast(1)
                .joinToString(separator = ", ") + " & " + nameList.last()
        }
    }


    @JvmStatic
    fun onItemClick(
        context: Context,
        itemPosition: Any,
        selectedItems: HashMap<String, String>,
        frontView: View,
        backView: View,
        buttonView: View,
        dismissView: View,
    ) {
        val selectedItemId: String? = when (itemPosition) {
            is AccountSetupDataModel -> itemPosition.mId
            is VendorModel -> itemPosition.id
            is LevelTable -> itemPosition.levelId
            is ClassNameTable -> itemPosition.classId
            else -> null
        }

        val itemName: String? = when (itemPosition) {
            is AccountSetupDataModel -> itemPosition.mAccountName
            is VendorModel -> itemPosition.customerName
            is LevelTable -> itemPosition.levelName
            is ClassNameTable -> itemPosition.className
            else -> null
        }

        if ((selectedItemId != null) && selectedItems.contains(selectedItemId)) {

            flipAnimation(
                context,
                frontView,
                backView,
                "left"
            )

            selectedItems.remove(selectedItemId)

            if (selectedItems.isEmpty()) {
                buttonView.isVisible = false
                dismissView.isVisible = true
            }

        } else {

            if (selectedItems.size == 3) {
                Toast.makeText(
                    context, "Only 3 items can be selected", Toast
                        .LENGTH_SHORT
                ).show()
            } else {

                flipAnimation(
                    context,
                    frontView,
                    backView,
                    "right"
                )

                buttonView.isVisible = true
                dismissView.isVisible = false

                if (selectedItemId != null && itemName != null) {
                    selectedItems[selectedItemId] = itemName
                }
            }
        }
    }


    @JvmStatic
    fun <T, K> groupBy(
        items: MutableList<T>,
        keyExtractor: (T) -> K
    ): MutableList<GroupItem<K, T>> {

        /**
         * the function takes two data types:  T and K
         * T is the type of items list supplied
         *  K represents the type of the grouping key
         * The keyExtractor parameter is a lambda function that accepts an item of type T and
         * returns the corresponding grouping key of type K.
         */

        val groupItems = mutableListOf<GroupItem<K, T>>()
        var currentGroup = mutableListOf<T>()
        var currentKey: K? = null

        items.forEach {
            val key = keyExtractor(it)

            if (key != currentKey) {
                if (currentGroup.isNotEmpty()) {
                    groupItems.add(GroupItem(currentKey, currentGroup))
                }

                currentKey = key
                currentGroup = mutableListOf()

            }

            currentGroup.add(it)
        }

        /**
         * After iterating through all the items, the currentGroup will contain the last group of
         * items with the same key. If currentGroup is not empty at this point, it means there are
         * items in the last group that haven't been added to groupedItems yet.
         *
         * To ensure that all the items are added properly, the condition is used to to add the
         * last item before return the groupItems.
         */

        if (currentGroup.isNotEmpty()) {
            groupItems.add(GroupItem(currentKey, currentGroup))
        }

        return groupItems
    }


    @JvmStatic
    fun formatOrdinalTerms(term: String): SpannableString {
        val spannableString = SpannableString(term)
        val ordinalSuffixes = arrayOf("st", "nd", "rd")
        // \\d+(st|nd|rd)

        for (suffix in ordinalSuffixes) {
            val startIndex = term.indexOf(suffix)
            if (startIndex >= 0) {
                val endIndex = startIndex + suffix.length
                spannableString.setSpan(
                    SuperscriptSpan(),
                    startIndex,
                    endIndex,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

        }

        return spannableString
    }


    @JvmStatic
    fun showSoftInput(context: Context, editText: EditText) {
        editText.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        editText.postDelayed({
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    @SuppressLint("ClickableViewAccessibility")
    @JvmStatic
    fun smoothScrollEditText(editText: EditText) {

        editText.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    true
                }

                else -> false
            }
        }
    }

    @JvmStatic
    fun hideKeyboard(editText: EditText, context: Context) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager

        editText.apply {
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
            clearFocus()
            setText("")
        }
    }


    @JvmStatic
    fun compareJsonObjects(jsonObject1: JSONObject, jsonObject2: JSONObject): Boolean {
        if (jsonObject1.length() != jsonObject2.length()) {
            return false
        }

        val keys = jsonObject1.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            if (!jsonObject2.has(key)) {
                return false
            }

            val value1 = jsonObject1.get(key)
            val value2 = jsonObject2.get(key)

            if (value1 is JSONObject && value2 is JSONObject) {
                if (!compareJsonObjects(value1, value2)) {
                    return false
                }
            } else if (value1 is JSONArray && value2 is JSONArray) {
                if (!compareJsonArrays(value1, value2)) {
                    return false
                }
            } else if (value1 != value2) {
                return false
            }
        }

        return true
    }

    @JvmStatic
    fun compareJsonArrays(jsonArray1: JSONArray, jsonArray2: JSONArray): Boolean {
        if (jsonArray1.length() != jsonArray2.length()) {
            return false
        }

        for (i in 0 until jsonArray1.length()) {
            val value1 = jsonArray1[i]
            val value2 = jsonArray2[i]

            if (value1 is JSONObject && value2 is JSONObject) {
                if (!compareJsonObjects(value1, value2)) {
                    return false
                }
            } else if (value1 is JSONArray && value2 is JSONArray) {
                if (!compareJsonArrays(value1, value2)) {
                    return false
                }
            } else if (value1 != value2) {
                return false
            }
        }

        return true
    }

    @JvmStatic
    fun encodeUriOrFileToBase64(imageUri: Any?, context: Context): Any? {
        val inputStream = when (imageUri) {
            is File -> FileInputStream(imageUri)
            else -> context.contentResolver.openInputStream(imageUri as Uri)
        }

        return inputStream.use { input ->
            try {
                val outputStream = ByteArrayOutputStream()
                val bufferedInput = BufferedInputStream(input)
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (bufferedInput.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                val fileBytes = outputStream.toByteArray()
                Base64.encodeToString(fileBytes, Base64.DEFAULT)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    @JvmStatic
    fun isBased64(encodedString: String): Boolean {
        return org.apache.commons.codec.binary.Base64.isBase64(encodedString)
    }

    @JvmStatic
    fun decodeBase64ToBitmap(encodedString: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
            Bitmap.createBitmap(
                BitmapFactory.decodeByteArray(
                    decodedBytes,
                    0,
                    decodedBytes.size
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}

interface VolleyCallback {
    fun onResponse(response: String)
    fun onError(error: VolleyError)
}