$(document).ready ->
  fromShip = new Vue(vueSettings)

timeout = 0

vueSettings =
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
    period: false
    start_period: moment({year: 2014, month: 0, day: 1}).format('YYYY-MM-DD')
    end_period: moment().format('YYYY-MM-DD')

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
      $.getJSON "/rest/v1/recipe/from_ship/#{sid}", @getFromTo(), (ret) =>
        @sCounts = ret
      $.getJSON "/rest/v1/drop_from_ship/#{sid}", @getFromTo(), (ret) =>
        @dropCounts = ret
    getICounts: (iid) ->
      @resetCounts()
      $.getJSON "/rest/v1/recipe/from_item/#{iid}", @getFromTo(), (ret) =>
        @iCounts = ret
    getCounts: ->
      if @shipId != -1 then @getSCounts(@shipId)
      else if @itemId != 1 then @getICounts(@itemId)
    resetCounts: ->
      @setHash()
      @sCounts = []
      @dropCounts = []
      @iCounts = []
    setHash: ->
      param = query: @query
      if @shipId != -1 then param.ship = @shipId
      else if @itemId != -1 then param.item = @itemId
      if @period
        param.start = @start_period
        param.end = @end_period
      location.hash = toURLParameter(param)
    restoreHash: ->
      param = fromURLParameter(location.hash.replace(/^\#/, ''))
      @query = if param.query? then decodeURIComponent(param.query) else @query
      @shipId = param.ship ? @shipId
      @itemId = param.item ? @itemId
      if param.start
        @period = true
        @start_period = param.start
        @end_period = param.end ? @end_period
    getFromTo: ->
      if @period
        {from: @start_period, to: @end_period}
      else
        {}

  created: ->
    @restoreHash()
    if @shipId != -1
      @getSCounts(@shipId)
    else if @itemId != -1
      @getICounts(@itemId)
    else if @query != ''
      timout = @searchShip(this, @query)()

  watch:
    query: (q) ->
      if q != ''
        clearTimeout(timeout)
        timeout = setTimeout(@searchShip(this, q), 500)
    shipId: (sid) ->
      if sid != -1
        @itemId = -1
        @getSCounts(sid)
      else
        ships = []
    itemId: (iid) ->
      if iid != -1
        @shipId = -1
        @getICounts(iid)
      else
        items = []
    start_period: (start) ->
      if @period
        console.log("OK")
        clearTimeout(timeout)
        timeout = setTimeout(@getCounts, 500)
    end_period: (end) ->
      if @period
        clearTimeout(timeout)
        timeout = setTimeout(@getCounts, 500)
    period: ->
      clearTimeout(timeout)
      timeout = setTimeout(@getCounts, 500)
