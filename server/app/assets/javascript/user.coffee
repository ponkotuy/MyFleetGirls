# Require graph.coffee

$(document).ready ->
  userid = $('#userid').val()
  fixWidth()
  graphExps(userid)
  graphScore(userid)

  $('button.delete-yome').each ->
    $(@).click ->
      shipId = $(@).attr('data-ship-id')
      data = {shipId: shipId, userId: userid}
      $.ajax('/passwd/post/v1/yome', {type: 'DELETE', data: data})
        .success ->
          location.reload(true)

graphExps = (userid) ->
  name = 'admiral_exp'
  $.getJSON "/rest/v1/#{userid}/basics", (data) ->
    if data.length > 2
      raw = transExps(data)
      graph = new Graph(name)
      graph.overviewPlot(raw)
      graph.wholePlot(raw)
    else
      noDataGraph($('#' + name))

graphScore = (userid) ->
  name = 'admiral_score'
  $.getJSON "/rest/v2/user/#{userid}/scores", (data) ->
    if data.length > 2
      raw = transRates(data)
      graph = new Graph(name)
      graph.overviewPlot(raw)
      graph.wholePlot(raw)
    else
      noDataGraph($('#' + name))

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
