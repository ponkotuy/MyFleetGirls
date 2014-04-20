$(document).ready ->
  $('div.sunburst').each ->
    json = $(this).attr('data-json')
    data = JSON.parse(json)
    viewSunburst($(this), data)

viewSunburst = (it, data) ->
  opt =
    sideSize: 960
    defaultMessage: 'Entire'
    levels: ['ship', 'class']
    sizeField: 'count'
  sunburst.createChart(data, opt, "sunbusrt-chart")
