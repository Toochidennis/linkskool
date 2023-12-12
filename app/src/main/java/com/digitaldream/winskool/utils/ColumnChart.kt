package com.digitaldream.winskool.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.ChartValue
import com.digitaldream.winskool.utils.FunctionUtils.webViewProgress

const val ASSET_PATH = "file:///android_asset/"

@SuppressLint("SetJavaScriptEnabled")
class ColumnChart @JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : WebView(context, attrs) {

    // chart attributes
    private var chartTitle: String? = null
    private var chartData: String? = null

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ColumnChart
        )
        setBackgroundColor(Color.WHITE)

        try {
            chartTitle = typedArray.getString(R.styleable.ColumnChart_chartTitle)

            /*colorsArray = intArrayOf(
                typedArray.getColor(
                    R.styleable.GraphView_gradientStartColor,
                    ContextCompat.getColor(context, R.color.startColor)
                ),
                typedArray.getColor(
                    R.styleable.GraphView_gradientEndColor,
                    ContextCompat.getColor(context, R.color.endColor)
                )
            )*/

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            typedArray.recycle()
        }
    }

    fun setChartData(chartValues: ArrayList<ChartValue>) {
        val builder = StringBuilder()
        for (item in chartValues) {
            builder.append("[")
            builder.append("'${item.y}'").append(", ")
            builder.append(item.x)
            builder.append("],\n")
        }
        chartData = if (builder.isNotEmpty()) {
            builder.deleteCharAt(builder.lastIndexOf(",")).toString()
        } else {
            ""
        }

        init()

    }


    private fun init() {

        val content = "<html>\n" +
                "<head>\n" +
                "    <!--Load the AJAX API-->\n" +
                "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "\n" +
                "      // Load the Visualization API and the corechart package.\n" +
                "      google.charts.load('current', {'packages':['corechart']});\n" +
                "\n" +
                "      // Set a callback to run when the Google Visualization API is loaded.\n" +
                "      google.charts.setOnLoadCallback(drawChart);\n" +
                "\n" +
                "      // Callback that creates and populates a data table,\n" +
                "      // instantiates the pie chart, passes in the data and\n" +
                "      // draws it.\n" +
                "      function drawChart() {\n" +
                "\n" +
                "        // Create the data table.\n" +
                "        var data = new google.visualization.DataTable();\n" +
                "        data.addColumn('string', 'Topping');\n" +
                "        data.addColumn('number', 'Average');\n" +
                "        data.addRows([\n" +
                "           $chartData \n" +
                "        ]);\n" +
                "\n" +
                "        // Set chart options\n" +
                "        var options = {'title':'$chartTitle',\n" +
                "                       'width':450,\n" +
                "                       'height':250,\n" +
                "                       'bar': {groupWidth: \"35%\"},\n" +
                "                       'legend': {position: \"none\"}\n" +
                "                       };\n" +
                "\n" +
                "        // Instantiate and draw our chart, passing in some options.\n" +
                "        var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));\n" +
                "        chart.draw(data, options);\n" +
                "      }\n" +
                "    </script>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<!--Div that will hold the column chart-->\n" +
                "<div id=\"chart_div\" style=\"width: 450px; height: 250px;\"></div>\n" +
                "</body>\n" +
                "</html>"


        settings.apply {
            domStorageEnabled = true
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
        }

        loadDataWithBaseURL(ASSET_PATH, content, "text/html", "UTF-8", null)

        requestFocusFromTouch()

        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                return false
            }
        }

        webViewProgress(context, this)

    }

    override fun destroy() {
        val parent = parent as ViewGroup
        parent.removeView(this)
        super.destroy()
    }

}
