
# Required Flot, MomentLocales, Lodash

nowGroup = 1
nowAgg = 2
groupButtons = []
plot = null

highlight = (node) -> node.removeClass('btn-default').addClass('btn-primary')
unhighlight = (node) -> node.removeClass('btn-primary').addClass('btn-default')

@setGroup = (n) ->
  unhighlight($('button.group-button'))
  nowGroup = n
  drawGraph()

@setAgg = (n) ->
  unhighlight($('button.agg-button'))
  nowAgg = n
  drawGraph()

setGroupButtonClick = ->
  $('button.group-button').each ->
    group = parseInt($(this).attr('data-group'))
    $(this).click ->
      setGroup(group)
      highlight($(this))

setAggButtonClick = ->
  $('button.agg-button').each ->
    agg = parseInt($(this).attr('data-agg'))
    $(this).click ->
      setAgg(agg)
      highlight($(this))

fixWidth = ->
  width = $('div.tab-content').width()
  $('.width-adj').width(width)

trans = (data) ->
  data.map (ship) ->
    label: ship.name
    data: ship.exps.map (x) -> [x['created'], x['exp']]

graphOption =
  xaxis: { mode: 'time', timezone: 'browser' }
  yaxes: [{}, { alignTicksWithAxis: 1, position: 'right' }]
  selection: { mode: 'x' }
  legend: { position: 'nw' }
  colors: ['red', 'green', 'blue', 'purple', 'gray']

drawGraph = ->
  userid = $('#userid').val()
  $.getJSON "/rest/v2/user/#{userid}/ship/exps/#{nowGroup}?agg=#{nowAgg}", (data) ->
    if data.length > 0
      raw = trans(data)
      plot = $.plot('#ship_exps_graph', raw, graphOption)

$(document).ready ->
  setGroupButtonClick()
  setAggButtonClick()
  fixWidth()
  drawGraph()
