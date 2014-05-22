$(document).ready ->
  userid = $('#userid').val()

  routeLog = new Vue
    el: '#routelog'
    data:
      routes: []
      count: 20
      allCount: 0
      page: 0
      cellInfo: []
      stage: 'ALL'
    methods:
      getInitData: () ->
        @fromHash()
        $.getJSON '/rest/v1/cell_info', (data) =>
          @cellInfo = data
        @getData()
      getData: () ->
        @setHash()
        cond = $.extend({}, {limit: @count, offset: @page*@count}, @areainfo())
        $.getJSON "/rest/v1/#{userid}/route_log_count", cond, (data) =>
          @allCount = data
        $.getJSON "/rest/v1/#{userid}/route_log", cond, (data) =>
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
        location.hash = toURLParameter(@areainfo())
      fromHash: () ->
        obj = fromURLParameter(location.hash.replace(/^\#/, ''))
        if obj.area? and obj.info?
          @stage = "#{obj.area}-#{obj.info}"
      setPage: (page) -> @page = page
      maxPage: () -> Math.min(Math.ceil(@allCount / @count), 10)
      pages: () -> [0..(@maxPage() - 1)]
    created: () ->
      @getInitData()
    ready: () ->
      @$watch 'stage', () -> @page = 0
      @$watch 'page', () -> @getData()
