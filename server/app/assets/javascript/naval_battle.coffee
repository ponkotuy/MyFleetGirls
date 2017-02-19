$(document).ready ->
  $('#modal').on 'hidden.bs.modal', ->
    $(this).removeData('bs.modal')

  userid = $('#userid').val()
  hashes =
    try
      JSON.parse(location.hash.replace(/^\#/, ''))
    catch
      try
        {battle: fromURLParameter(location.hash.replace(/^\#/, ''))}
      catch
        {}
  syncHash = () ->
    location.hash = JSON.stringify(hashes)
  battle = new Vue
    el: '#battle_log'
    data:
      count: 20
      allCount: 0
      page: 0
      data: []
      bossOnly: false
      dropOnly: false
      stage: 'ALL'
      ranks: {S: true, A: true, B: true, C: false, D: false, E: false, N: false}
    methods:
      setPage: (page) ->
        @page = page
      getData: () ->
        @data = []
        @setHash()
        dat = $.extend(@whereObj(), @areainfo())
        $.getJSON "/rest/v1/#{userid}/battle_result_count", dat, (ret) =>
          @allCount = ret
        $.getJSON "/rest/v1/#{userid}/battle_result", dat, (ret) =>
          @data = ret
      timeToStr: (millis) ->
        moment(millis).format('YYYY-MM-DD HH:mm')
      maxPage: -> Math.min(Math.ceil(@allCount / @count), 10)
      pages: -> [0..(@maxPage() - 1)]
      whereObj: ->
        limit: @count
        offset: @page*@count
        boss: @bossOnly
        drop: @dropOnly
        rank: @rank()
      rank: ->
        xs = for str, value of @ranks
          if value then str else ''
        xs.join('')
      areainfo: () ->
        if @stage == 'ALL'
          {}
        else
          xs = @stage.split('-')
          {area: xs[0], info: xs[1]}
      setHash: ->
        obj =
          count: @count
          page: @page
          bossOnly: @bossOnly
          dropOnly: @dropOnly
          rank: @rank()
        hashes.battle = $.extend(obj, @areainfo())
        syncHash()
      restoreHash: ->
        unless hashes.battle? and hashes.battle.count? then return
        obj = hashes.battle
        @count = parseInt(obj.count)
        @setPage(parseInt(obj.page))
        @bossOnly = obj.bossOnly != 'false'
        @dropOnly = obj.dropOnly != 'false'
        if obj.area? and obj.info?
          @stage = "#{obj.area}-#{obj.info}"
        for str, _ of @ranks
          @ranks[str] = obj.rank.indexOf(str) != -1
    compiled: ->
      @restoreHash()
      @getData()
    ready: ->
      @$watch 'ranks', @getData, true # Deep Watch
    watch:
      'page': -> @getData()
      'bossOnly': -> @getData()
      'dropOnly': -> @getData()
      'stage': -> @getData()

  routeLog = new Vue
    paramAttributes: ['data-userid']
    el: '#routelog'
    data:
      routes: []
      count: 20
      allCount: 0
      page: 0
      cellInfo: []
      stage: 'ALL'
      fleetType: 'name'
    methods:
      getInitData: () ->
        @restoreHash()
        $.getJSON '/rest/v1/cell_info', (data) =>
          @cellInfo = data
        @getData()
      getData: () ->
        @setHash()
        cond = $.extend({}, {limit: @count, offset: @page*@count}, @areainfo())
        $.getJSON "/rest/v1/#{@userid}/route_log_count", cond, (data) =>
          @allCount = data
        $.getJSON "/rest/v1/#{@userid}/route_log", cond, (data) =>
          @routes = []
          data.forEach (d) => @routes.push(d)
      viewCell: (area, info, cell) ->
        cInfo = (@cellInfo.filter (c) -> (c.areaId == area and c.infoNo == info and c.cell == cell))[0]
        "#{cell}" +
          if cInfo?
            "(#{cInfo.alphabet})" +
              if cInfo.start then ' <small>Start</small>' else '' +
                if cInfo.boss then ' <small>BOSS</small>' else ''
          else
            ''
      timeToStr: (millis) ->
        moment(millis).format('YYYY-MM-DD HH:mm')
      areainfo: () ->
        if @stage == 'ALL'
          {}
        else
          xs = @stage.split('-')
          {area: xs[0], info: xs[1]}
      setHash: () ->
        obj = if @fleetType != 'name'
          $.extend(@areainfo(), {ftype: @fleetType})
        else
          @areainfo()
        obj.page = @page
        hashes.routeLog = obj
        syncHash()
      restoreHash: () ->
        obj = if hashes.routeLog? then hashes.routeLog else {}
        if obj.area? and obj.info?
          @stage = "#{obj.area}-#{obj.info}"
        @fleetType = obj.ftype ? @fleetType
        @page = obj.page ? @page
      setPage: (page) -> @page = page
      maxPage: () -> Math.min(Math.ceil(@allCount / @count), 10)
      pages: () -> [0..(@maxPage() - 1)]
    compiled: () ->
      @getInitData()
    watch:
      'stage': ->
        if @page == 0
          @getData()
        else
          @setPage(0)
      'page': -> @getData()
      'fleetType': -> @setHash()
