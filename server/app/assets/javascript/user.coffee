
# Require graph.coffee

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
