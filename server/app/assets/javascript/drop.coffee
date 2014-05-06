$(document).ready ->
  $('.panel').each ->
    elem = $(this)
    id = elem.attr('id')
    cell = elem.attr('data-cell')
    vue = new Vue
      el: '#' + id
      data:
        drops: []
        dropOnly: false
        rank_s: true
        rank_a: true
        rank_b: true
        url: ''
      methods:
        rank: ->
          (if @rank_s then 'S' else '') +
            (if @rank_a then 'A' else '') +
            (if @rank_b then 'B' else '')
        getJSON: ->
          @setHash()
          url = @url.replace('(rank)', @rank())
          $.getJSON url, (data) =>
            xs = if @dropOnly then _.filter(data, (drop) -> drop.getShipName?) else data
            @drops = xs.reverse().map (it) ->
              it.getShipName ?= 'ドロップ無し'
              it
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
          location.hash = toURLParameter({cell: cell, rank: @rank(), dropOnly: @dropOnly})
        restoreHash: ->
          obj = fromURLParameter(location.hash.replace(/^\#/, ''))
          unless obj.cell? then return
          @dropOnly = obj.dropOnly == 'true'
          unless obj.rank? then return
          @rank_s = obj.rank.indexOf('S') != -1
          @rank_a = obj.rank.indexOf('A') != -1
          @rank_b = obj.rank.indexOf('B') != -1
      created: ->
        i = this
        elem.find('.panel-collapse').on 'show.bs.collapse', ->
          i.restoreHash()
          i.url = $(this).attr('data-url')
          if i.drops.length == 0
            i.getJSON()
      ready: ->
        @$watch 'dropOnly', ->
          @getJSON()
        @$watch 'rank_s', ->
          @getJSON()
        @$watch 'rank_a', ->
          @getJSON()
        @$watch 'rank_b', ->
          @getJSON()
        @$watch 'drops', (drops) ->
          if drops.length > 0
            $("#panel#{cell}")[0].scrollIntoView(true)
            @draw()

  obj = fromURLParameter(location.hash.replace(/^\#/, ''))
  $("#collapse#{obj.cell}").collapse()
