$(document).ready ->
  userid = $('#userid').val()
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
      ranks: {S: true, A: true, B: true, C: false, D: false, E: false}
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
        location.hash = toURLParameter($.extend(obj, @areainfo()))
      restoreHash: ->
        obj = fromURLParameter(location.hash.replace(/^\#/, ''))
        unless obj.count? then return
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
    ready: ->
      @$watch 'ranks', @getData, true # Deep Watch
    watch:
      'page': -> @getData()
      'bossOnly': -> @getData()
      'dropOnly': -> @getData()
      'stage': -> @getData()
