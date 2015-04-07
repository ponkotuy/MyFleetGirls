
$(document).ready ->
  userid = $('#userid').val()
  fixWidth()
  graphOpts.forEach (obj) ->
    $.getJSON obj.url(userid), (data) ->
      if data.length > 2
        raw = obj.trans(data)
        obj.graph = new Graph(obj.name)
        obj.graph.overviewPlot(raw)
        obj.graph.wholePlot(raw)
      else
        noDataGraph($('#' + obj.name))

  $('button.delete-yome').each ->
    $(@).click ->
      shipId = $(@).attr('data-ship-id')
      data = {shipId: shipId, userId: userid}
      $.ajax('/passwd/post/v1/yome', {type: 'DELETE', data: data})
        .success ->
          location.reload(true)

transExps = (data) -> [
  data: data.map (x) -> [x['created'], x['experience']]
  label: '提督経験値'
]

transRates = (data) -> [
  data: data.map (x) -> [x['created'], x['rate']]
  label: '戦果'
]

fixWidth = ->
  width = $('div.tab-content').width()
  $('.width-adj').width(width)

noDataGraph = (node) ->
  node.replaceWith('<p>グラフ生成に必要なデータが充分にありません</p>')

# data required {userId: `userId`, shipId: `shipId`}
@yome = (userId, shipId) ->
  $.post('/passwd/post/v1/settings', {userId: userId, shipId: shipId})
    .success -> location.href = "/user/#{userId}/top"
    .error (e) -> console.error(e)

option =
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
  colors: ['red']
optionO =
  series: { lines: { show: true, lineWidth: 1 }, shadowSize: 0 }
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
  colors: ['red']

graphOpts = [
  {
    url: (userid) -> "/rest/v1/#{userid}/basics"
    trans: transExps
    name: 'admiral_exp'
  },
  {
    url: (userid) -> "/rest/v2/user/#{userid}/scores"
    trans: transRates
    name: 'admiral_score'
  }
]

class Graph
  chart: ''
  overview: ''

  constructor: (@name) ->
    @chart = '#' + @name + '_graph'
    @overview = '#' + @name + '_overview'

  plot: null

  wholePlot: (raw) ->
    @mainPlot raw, 'whole', moment().subtract(1, 'months').valueOf()

  mainPlot: (raw, active, min, max = moment().valueOf()) ->
    min_ = min ?= 0
    rangeData = raw[0].data.filter (x) -> min_ < x[0] and x[0] < max
    rangeExps = rangeData.map (x) -> x[1]
    option.yaxis = {min: _.min(rangeExps)}
    first = _.min(rangeData.map (x) -> x[0])
    if min?
      min = Math.max(min, first)
      newOpt = $.extend true, {}, option,
        xaxis: { min: min, max: max }
      @plot = $.plot(@chart, raw, newOpt)

      # Delete plot if no data
      y = @plot.getYAxes()[0]
      yDiff = y.max - y.min
      if yDiff < 1
        $(@chart).replaceWith('')
    else
      @plot = $.plot(@chart, raw, option)

  overviewPlot: (raw) ->
    $.plot(@overview, raw, optionO)
