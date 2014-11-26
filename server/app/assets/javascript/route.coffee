$(document).ready () ->
  vue = new Vue(vueConf('#route_table'))

  $('#modal').on 'hidden.bs.modal', ->
    vue.modal = false
    vue.setHash()
    $(this).removeData('bs.modal')

depdest = () ->
  dep = parseInt($('#dep_dest').attr('data-dep'))
  dest = parseInt($('#dep_dest').attr('data-dest'))
  {dep: dep, dest: dest}

timeout = 0

vueConf = (id) ->
  el: id

  data:
    routes: []
    cellInfo: []
    counts: []
    area: 0
    info: 0
    period: false
    from: moment({year: 2014, month: 0, day: 1}).format('YYYY-MM-DD')
    to: moment().format('YYYY-MM-DD')
    modal: false
    dep: 0
    dest: 0

  methods:
    getJSON: () ->
      $.getJSON "/rest/v1/route/#{@area}/#{@info}", @periodObj(), (data) =>
        @routes = data
        sumCounts = []
        data.forEach (d) ->
          sumCounts[d.dep] ?= 0
          sumCounts[d.dep] += d.count
        @counts = sumCounts
      $.getJSON "/rest/v1/cell_info", {area: @area, info: @info}, (data) =>
        @cellInfo = data
      @setHash({})
    viewCell: (cell) ->
      cInfo = (@cellInfo.filter (c) -> c.cell == cell)[0]
      "#{cell}" +
        if cInfo?
          "(#{cInfo.alphabet})" +
            if cInfo.start then ' <small>Start</small>' else '' +
              if cInfo.boss then ' <small>BOSS</small>' else ''
        else
          ''
    viewRate: (route) ->
      v = route.count / @counts[route.dep] * 100
      v.toFixed(1) + '%'
    loadAttr: (el) ->
      @area = parseInt($(el).attr('data-area'))
      @info = parseInt($(el).attr('data-info'))
    setHash: ->
      param = if @modal then {modal: @modal, dep: @dep, dest: @dest} else {}
      location.hash = toURLParameter($.extend(param, @periodObj()))
    restoreHash: ->
      param = fromURLParameter(location.hash.replace(/^\#/, ''))
      if param.from?
        @period = true
        @from = param.from
        @to = param.to ? @to
      if param.modal?
        @modal = true
        @dep = param.dep ? @dep
        @dest = param.dest ? @dest
        @modaling(param)
    periodObj: -> if @period then {from: @from, to: @to} else {}
    modaling: (route) ->
      url = @modalURL(route)
      $('#modal').modal({remote: url})
      @modal = true
      @dep = route.dep
      @dest = route.dest
      @setHash()
    modalURL: (route) ->
      base = "/entire/sta/route_fleet/#{@area}/#{@info}/#{route.dep}/#{route.dest}"
      result = base + if @period then "?from=#{@from}&to=#{@to}" else ""
      result
    change: ->
      if @period
        clearTimeout(timeout)
        timeout = setTimeout(@getJSON, 500)

  created: ->
    @loadAttr(id)
    @restoreHash()
    timeout = 0

  watch:
    period: -> @getJSON()
    from: -> @change()
    to: -> @change()
