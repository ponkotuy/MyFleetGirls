$(document).ready ->
  $('.panel').each ->
    elem = $(this)
    id = elem.attr('id')
    vue = new Vue
      el: '#' + id
      data:
        drops: []
        rank_s: true
        rank_a: true
        rank_b: true
        url: ""
      methods:
        rank: ->
          (if @rank_s then "S" else "") +
            (if @rank_a then "A" else "") +
            (if @rank_b then "B" else "")
        getJSON: ->
          url = @url.replace('(rank)', @rank())
          $.getJSON url, (data) =>
            @drops = data.map (it) ->
              it.getShipName ?= 'ドロップ無し'
              it
      created: ->
        i = this
        elem.find('.panel-collapse').on 'show.bs.collapse', ->
          i.url = $(this).attr('data-url')
          if i.drops.length == 0
            i.getJSON()
      ready: ->
        @$watch 'rank_s', ->
          @getJSON()
        @$watch 'rank_a', ->
          @getJSON()
        @$watch 'rank_b', ->
          @getJSON()
