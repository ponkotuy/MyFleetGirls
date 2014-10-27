chart = '#material_chart'
overview = '#material_overview'
chart2 = '#material_chart2'
overview2 = '#material_overview2'

option =
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
  colors: ['red', 'green', 'blue', 'purple']
optionO =
  series: { lines: { show: true, lineWidth: 1 }, shadowSize: 0 }
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
  colors: ['red', 'green', 'blue', 'purple']

plot = null
table = []

$(document).ready ->
  userid = $('#userid').val()
  fixWidth()
  button()
  $.getJSON "/rest/v1/#{userid}/materials", (data) ->
    table = translate(data)

    param = fromURLParameter(location.hash.replace(/^\#/, ''))
    if param.max?
      mainPlot(param.active, param.min, param.max)
    else
      if data[0].created < moment().subtract('month', 1).valueOf()
        monthPlot()
      else
        wholePlot()
    $(chart).bind 'plotselected', (event, ranges) ->
      mainPlot('', ranges.xaxis.from, ranges.xaxis.to)

    plotO = $.plot(overview, table[0], optionO)
    plotO2 = $.plot(overview2, table[1], optionO)
    $(overview).bind('plotselected', (event, ranges) -> plot.setSelection(ranges))

translate = (data) ->
  fuel = { data: transElem(data, 'fuel'), label: '燃料' }
  ammo = { data: transElem(data, 'ammo'), label: '弾薬' }
  steel = { data: transElem(data, 'steel'), label: '鉄鋼' }
  bauxite = { data: transElem(data, 'bauxite'), label: 'ボーキサイト' }
  instant = { data: transElem(data, 'instant'), label: '高速建造材' }
  bucket = { data: transElem(data, 'bucket'), label: '高速修復材' }
  develop = { data: transElem(data, 'develop'), label: '開発資材' }
  revamping = { data: transElem(data, 'revamping'), label: '改修資材' }
  [[fuel, ammo, steel, bauxite], [instant, bucket, develop, revamping]]

transElem = (data, elem) -> data.map (x) -> [x['created'], x[elem]]

monthPlot = -> mainPlot 'month', moment().subtract('months', 1).valueOf()
weekPlot = -> mainPlot 'week', moment().subtract('weeks', 1).valueOf()
dayPlot = -> mainPlot 'day', moment().subtract('days', 1).valueOf()
wholePlot = -> mainPlot 'whole'

mainPlot = (active, min, max = moment().valueOf()) ->
  activeButton(active)
  location.hash = toURLParameter({min: min, max: max, active: active})
  if min?
    newOpt = $.extend true, {}, option,
      xaxis: { min: min, max: max }
    plot = $.plot(chart, table[0], newOpt)
    plot2 = $.plot(chart2, table[1], newOpt)
  else
    plot = $.plot(chart, table[0], option)
    plot2 = $.plot(chart2, table[1], option)

activeButton = (active) ->
  $('#range').children('button.active').map () -> $(this).removeClass('active')
  $('#' + active).addClass('active')

button = ->
  $('#whole').click ->
    mainPlot('whole')
  $('#month').click ->
    monthPlot()
  $('#week').click ->
    weekPlot()
  $('#day').click ->
    dayPlot()

fixWidth = ->
  width = $('div.tab-content').width()
  $('.width-adj').width(width)
