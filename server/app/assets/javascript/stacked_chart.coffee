$(document).ready ->
  $('div.stacked').each ->
    json = $(this).attr('data-json')
    data = JSON.parse(json)
    option =
      series:
        stack: true
      bars:
        show: true
        barWidth: 10
        color: 'blue'
      xaxis:
        tickSize: 10
        axisLabelFontSizePixels: 24
        axisLabel: 'Lv'
      yaxis:
        axisLabel: 'Count'
      grid:
        hoverable: true
        clickable: true

    $.plot($(this), [{color: 'blue', data: data}], option)
    $(this).bind 'plothover', (event, pos, item) ->
      if item?
        document.body.style.cursor = 'pointer'
      else
        document.body.style.cursor = 'default'
    $(this).bind "plotclick", (event, pos, item) ->
      min = item.datapoint[0]
      max = min + 9
      location.href = "ship#lv=#{min}%20-%20#{max}"
