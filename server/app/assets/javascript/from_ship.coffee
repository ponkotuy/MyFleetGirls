$(document).ready ->
  timeout = 0
  fromShip = new Vue
    el: '#from_name'

    data:
      query: ''
      ships: []
      items: []
      shipId: -1
      itemId: -1
      sCounts: []
      dropCounts: []
      iCounts: []

    methods:
      searchShip: (that, q) ->
        () ->
          $.getJSON '/rest/v1/search_master', {q: q}, (ret) =>
            that.resetCounts()
            that.ships = ret.ships
            that.items = ret.items
            if ret.ships.length == 1
              that.shipId = ret.ships[0].id
            else if ret.items.length == 1
              that.itemId = ret.items[0].id
      selectShip: (sid) ->
        @shipId = sid
      selectItem: (iid) ->
        @itemId = iid
      getSCounts: (sid) ->
        @resetCounts()
        $.getJSON "/rest/v1/recipe/from_ship/#{sid}", {}, (ret) =>
          @sCounts = ret
        $.getJSON "/rest/v1/drop_from_ship/#{sid}", {}, (ret) =>
          @dropCounts = ret
      getICounts: (iid) ->
        @resetCounts()
        $.getJSON "/rest/v1/recipe/from_item/#{iid}", {}, (ret) =>
          @iCounts = ret
      resetCounts: ->
        @sCounts = []
        @dropCounts = []
        @iCounts = []

    created: ->
      @query = $('#search').val()
      @$watch 'query', (q) ->
        if q != ''
          clearTimeout(timeout)
          timeout = setTimeout(@searchShip(this, q), 500)
      @$watch 'shipId', (sid) ->
        if sid != -1
          @itemId = -1
          @getSCounts(sid)
      @$watch 'itemId', (iid) ->
        if iid != -1
          @shipId = -1
          @getICounts(iid)
