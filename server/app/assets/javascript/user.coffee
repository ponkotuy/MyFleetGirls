chart = '#admiral_exp_graph'
overview = '#admiral_exp_overview'

option =
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
  colors: ["red"]
optionO =
  series: { lines: { show: true, lineWidth: 1 }, shadowSize: 0 }
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
  colors: ["red"]

plot = null
exps = []

$(document).ready ->
  userid = $('#userid').val()
  fixWidth()
  $.getJSON "/rest/v1/#{userid}/basics", (data) ->
    if data.length > 2
      exps = translate(data)
      plotO = $.plot(overview, exps, optionO)
      wholePlot()
    else
      $('#admiral_exp').replaceWith('<p>グラフ生成に必要なデータが充分にありません</p>')

  $('button.delete-yome').each ->
    $(@).click ->
      shipId = $(@).attr('data-ship-id')
      data = {shipId: shipId, userId: userid}
      $.ajax('/passwd/post/v1/yome', {type: 'DELETE', data: data})
        .success ->
          location.reload(true)


translate = (data) -> [
  data: data.map (x) -> [x['created'], x['experience']]
  label: '提督経験値'
]

wholePlot = -> mainPlot 'whole', moment().subtract(1, 'months').valueOf()

mainPlot = (active, min, max = moment().valueOf()) ->
  min_ = min ?= 0
  rangeData = exps[0].data.filter (x) -> min_ < x[0] and x[0] < max
  rangeExps = rangeData.map (x) -> x[1]
  option.yaxis = {min: _.min(rangeExps)}
  first = _.min(rangeData.map (x) -> x[0])
  if min?
    min = Math.max(min, first)
    newOpt = $.extend true, {}, option,
      xaxis: { min: min, max: max }
    plot = $.plot(chart, exps, newOpt)

    # Delete plot if no data
    y = plot.getYAxes()[0]
    yDiff = y.max - y.min
    if yDiff < 1
      $(chart).replaceWith('')
  else
    plot = $.plot(chart, exps, option)

fixWidth = ->
  width = $('div.tab-content').width()
  $('.width-adj').width(width)
