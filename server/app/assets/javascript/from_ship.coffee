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
        @setHash()
        @sCounts = []
        @dropCounts = []
        @iCounts = []
      setHash: ->
        param = query: @query
        if @shipId != -1 then param.ship = @shipId
        else if @itemId != -1 then param.item = @itemId
        location.hash = toURLParameter(param)
      restoreHash: ->
        param = fromURLParameter(location.hash.replace(/^\#/, ''))
        @query = param.query ? @query
        @shipId = param.ship ? @shipId
        @itemId = param.item ? @itemId

    created: ->
      @restoreHash()
      if @shipId != -1
        @getSCounts(@shipId)
      else if @itemId != -1
        @getICounts(@itemId)
      else if @query != ''
        timout = @searchShip(this, @query)()

    ready: ->
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
