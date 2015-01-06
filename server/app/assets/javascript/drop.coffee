$(document).ready ->
  $('.panel').each ->
    elem = $(this)
    id = elem.attr('id')
    cell = elem.attr('data-cell')
    vue = new Vue(vueConf(elem, id, cell))
  obj = fromURLParameter(location.hash.replace(/^\#/, ''))
  $("#collapse#{obj.cell}").collapse()
  $('.collapse').on 'hide.bs.collapse', ->
    here = location.href.replace(/\#.*$/, '') # hash以下を削除
    history.replaceState(null, null, here)

timeout = 0

vueConf = (elem, id, cell) ->
  el: '#' + id

  data:
    drops: []
    dropOnly: false
    rank_s: true
    rank_a: true
    rank_b: true
    url: ''
    period: false
    from: moment({year: 2014, month: 0, day: 1}).format('YYYY-MM-DD')
    to: moment().format('YYYY-MM-DD')

  methods:
    rank: ->
      (if @rank_s then 'S' else '') +
        (if @rank_a then 'A' else '') +
        (if @rank_b then 'B' else '')
    getJSON: ->
      @setHash()
      url = decodeURIComponent(@url.replace('(rank)', @rank()))
      if @period
        url = url.replace('(from)', @from)
        url = url.replace('(to)', @to)
      $.getJSON url, (data) =>
        xs = if @dropOnly then _.filter(data, (drop) -> drop.getShipName?) else data
        @drops = xs.reverse().map (it) ->
          it.getShipName ?= 'ドロップ無し'
          it
        @drops.sort (x, y) -> y.count - x.count
    draw: ->
      countSum = @countUpDrops(@drops)
      typed = _.groupBy @drops, (drop) -> drop.getShipType
      types = for type, ships of typed
        if type == 'undefined'
          count = ships[0].count
          name: "ドロップ無し #{@viewCount(count, countSum)}", count: count
        else
          sum = @countUpDrops(ships)
          children = ships.map (ship) =>
            name: "#{ship.getShipName} #{@viewCount(ship.count, countSum)}"
            count: ship.count
          name: "#{type} #{@viewCount(sum, countSum)}", children: children
      data = name: "ALL #{countSum}(100%)", children: types
      id = '#' + elem.find('.sunburst').attr('id')
      $(id).empty()
      drawSunburst(900, 600, id, data)
    countUpDrops: (drops) ->
      counts = drops.map (drop) -> drop.count
      _.reduce counts, (x, y) -> x + y
    viewCount: (elem, sum) -> "#{elem}(#{Math.round(elem/sum*1000)/10}%)"
    setHash: ->
      obj = {cell: cell, rank: @rank(), dropOnly: @dropOnly}
      if @period
        obj.from = @from
        obj.to = @to
      location.hash = toURLParameter(obj)
    restoreHash: ->
      obj = fromURLParameter(location.hash.replace(/^\#/, ''))
      if obj.cell?
        @dropOnly = obj.dropOnly == 'true'
      if obj.rank?
        @rank_s = obj.rank.indexOf('S') != -1
        @rank_a = obj.rank.indexOf('A') != -1
        @rank_b = obj.rank.indexOf('B') != -1
      if obj.from?
        @period = true
        @from = obj.from
        @to = obj.to ? @to
    clickDrop: (drop) ->
      base = "/entire/sta/from_ship#query=#{drop.getShipName}&ship=#{drop.getShipId}"
      url = base + if @period then "&from=#{@from}&to=#{@to}" else ''
      location.href = url

  created: ->
    i = this
    elem.find('.panel-collapse').on 'show.bs.collapse', ->
      i.restoreHash()
      i.url = $(this).attr('data-url')
      if i.drops.length == 0
        i.getJSON()
      else
        i.setHash()

  watch:
    dropOnly: -> @getJSON()
    rank_s: -> @getJSON()
    rank_a: -> @getJSON()
    rank_b: -> @getJSON()
    drops: (drops) ->
      if drops.length > 0
        $("#panel#{cell}")[0].scrollIntoView(true)
        @draw()
    period: -> @getJSON()
    from: ->
      if @period
        clearTimeout(timeout)
        timeout = setTimeout(@getJSON, 500)
    to: ->
      if @period
        clearTimeout(timeout)
        timeout = setTimeout(@getJSON, 500)
