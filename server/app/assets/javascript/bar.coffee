$(document).ready ->
  $('.ship_hbar').each () ->
    data = JSON.parse($(this).attr('data-json'))
    option =
      seriesDefaults:
        renderer: $.jqplot.BarRenderer
        rendererOptions:
          barDirection: 'horizontal'
          varyBarColor: true
          barMargin: 3
        shadow: false
        pointLabels:
          show: true
      axes:
        yaxis:
          renderer: $.jqplot.CategoryAxisRenderer
        xaxis:
          min: 0
          max: 120
          showTicks: false
          showTickMarks: false
    $(this).jqplot(data, option)
