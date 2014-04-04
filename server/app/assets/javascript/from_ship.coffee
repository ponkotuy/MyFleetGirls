$(document).ready ->
  timeout = 0
  fromShip = new Vue
    el: '#from_recipe'
    data:
      query: ''
      ships: []
      shipId: -1
      counts: []
    methods:
      searchShip: (that, q) ->
        () ->
          $.getJSON '/rest/v1/search_master_ship', {q: q}, (ret) =>
            that.ships = ret
            if ret.length == 1
              that.shipId = ret[0].id
      selectShip: (sid) ->
        @shipId = sid
      getCounts: (sid) ->
        $.getJSON "/rest/v1/recipe/from_ship/#{sid}", {}, (ret) =>
          @counts = ret
    created: ->
      @$watch 'query', (q) ->
        if q != ''
          clearTimeout(timeout)
          timeout = setTimeout(@searchShip(this, q), 500)
      @$watch 'shipId', (sid) ->
        if sid != -1
          @getCounts(sid)
