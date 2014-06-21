$(document).ready ->
  $('div.stacked').each ->
    json = $(this).attr('data-json')
    type = $(this).attr('data-type')
    xaxis = $(this).attr('data-xaxis')
    yaxis = $(this).attr('data-yaxis')
    xmax = $(this).attr('data-xmax')
    ymax = $(this).attr('data-ymax')
    clickable = $(this).attr('data-clickable') != 'false'
    data = JSON.parse(json)
    option =
      series:
        stack: true
      lines:
        show: type == 'steps' || type == 'lines'
        fill: true
        steps: type == 'steps'
      bars:
        show: type == 'bars'
        barWidth: 10
        color: 'blue'
      xaxis:
        tickSize: 10
        max: xmax
        axisLabelFontSizePixels: 24
        axisLabel: xaxis
      yaxis:
        max: ymax
        axisLabel: yaxis
      grid:
        hoverable: true
        clickable: clickable

    $.plot($(this), [{color: 'blue', data: data}], option)
    if clickable
      $(this).bind 'plothover', (event, pos, item) ->
        if item?
          document.body.style.cursor = 'pointer'
        else
          document.body.style.cursor = 'default'
      $(this).bind "plotclick", (event, pos, item) ->
        min = item.datapoint[0]
        max = min + 9
        location.href = "ship#lv=#{min}%20-%20#{max}"
