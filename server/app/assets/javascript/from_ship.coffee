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
    isDropOver: false
    DropSizeMax: 50
    iCounts: []
    period: false
    start_period: moment({year: 2014, month: 0, day: 1}).format('YYYY-MM-DD')
    end_period: moment().format('YYYY-MM-DD')

  methods:
    searchShip: (that, q) ->
      () ->
        $.getJSON '/rest/v1/search_master', {q: q}, (ret) ->
          that.resetCounts()
          that.ships = ret.ships
          that.items = ret.items
          if ret.ships.length == 1
            that.shipId = ret.ships[0].id
          else if ret.items.length == 1
            that.itemId = ret.items[0].id
          else
            that.shipId = -1
            that.itemId = -1
    selectShip: (sid) ->
      @shipId = sid
    selectItem: (iid) ->
      @itemId = iid
    getSCounts: (sid) ->
      @resetCounts()
      $.getJSON "/rest/v1/recipe/from_ship/#{sid}", @getFromTo(), (ret) =>
        @sCounts = ret
      $.getJSON "/rest/v1/drop_from_ship/#{sid}", @getFromTo(), (ret) =>
        @dropCounts = ret.slice(0, @DropSizeMax)
        @isDropOver = ret.length > @DropSizeMax
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
      param = query: encodeURIComponent(@query)
      if @shipId != -1 then param.ship = @shipId
      else if @itemId != -1 then param.item = @itemId
      if @period
        param.from = @start_period
        param.to = @end_period
      location.hash = toURLParameter(param)
    restoreHash: ->
      param = fromURLParameter(location.hash.replace(/^\#/, ''))
      @query = if param.query? then decodeURIComponent(param.query) else @query
      @shipId = param.ship ? @shipId
      @itemId = param.item ? @itemId
      if param.from
        @period = true
        @start_period = param.from
        @end_period = param.to ? @end_period
    getFromTo: ->
      if @period
        {from: @start_period, to: @end_period}
      else
        {}
    clickShip: (c) ->
      base = "/entire/sta/cship/#{c.mat.fuel}/#{c.mat.ammo}/#{c.mat.steel}/#{c.mat.bauxite}/#{c.mat.develop}"
      url = base + @fromToURL('?')
      location.href = url
    clickDrop: (c) ->
      location.href = dropUrl(c.cell) + @fromToURL('&')
    clickItem: (c) ->
      base = "/entire/sta/citem/#{c.mat.fuel}/#{c.mat.ammo}/#{c.mat.steel}/#{c.mat.bauxite}/#{c.mat.sTypeName}"
      url = base + @fromToURL('?')
      location.href = url
    fromToURL: (head) -> if @period then "#{head}from=#{@start_period}&to=#{@end_period}" else ''
    submit: ->
      @searchShip(this, @query)()

  ready: ->
    @restoreHash()
    if @shipId != -1
      @getSCounts(@shipId)
      clearTimeout(timeout)
    else if @itemId != -1
      @getICounts(@itemId)
      clearTimeout(timeout)
    else if @query != ''
      timeout = @searchShip(this, @query)()

  watch:
    shipId: (sid) ->
      if sid != -1
        @itemId = -1
        clearTimeout(timeout) # 起動時にquery発行されてscountが消されるのを抑制
        @getSCounts(sid)
      else
        @ships = []
    itemId: (iid) ->
      if iid != -1
        @shipId = -1
        clearTimeout(timeout)
        @getICounts(iid)
      else
        @items = []
    period: ->
      clearTimeout(timeout)
      timeout = setTimeout(@getCounts, 500)

dropUrl = (cell) ->
  "/entire/sta/drop/#{cell.area}/#{cell.info}/#cell=#{cell.area}-#{cell.info}-#{cell.cell}&rank=#{cell.rank}"
