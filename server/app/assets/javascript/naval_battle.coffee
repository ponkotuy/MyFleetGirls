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
      rankS: true
      rankA: true
      rankB: true
      rankC: false
      rankD: false
      rankE: false
    methods:
      setPage: (page) ->
        @page = page
      getData: () ->
        @setHash()
        dat = @whereObj()
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
        (if @rankS then 'S' else '') +
          (if @rankA then 'A' else '') +
          (if @rankB then 'B' else '') +
          (if @rankC then 'C' else '') +
          (if @rankD then 'D' else '') +
          (if @rankE then 'E' else '')
      setHash: ->
        obj =
          count: @count
          page: @page
          bossOnly: @bossOnly
          dropOnly: @dropOnly
          rank: @rank()
        location.hash = toURLParameter(obj)
      restoreHash: ->
        obj = fromURLParameter(location.hash.replace(/^\#/, ''))
        unless obj.count? then return
        @count = parseInt(obj.count)
        @page = parseInt(obj.page)
        @bossOnly = obj.bossOnly != 'false'
        @dropOnly = obj.dropOnly != 'false'
        @rankS = obj.rank.indexOf('S') != -1
        @rankA = obj.rank.indexOf('A') != -1
        @rankB = obj.rank.indexOf('B') != -1
        @rankC = obj.rank.indexOf('C') != -1
        @rankD = obj.rank.indexOf('D') != -1
        @rankE = obj.rank.indexOf('E') != -1
    created: ->
      @restoreHash()
      @getData()
    ready: ->
      @$watch 'page', () -> @getData()
      @$watch 'bossOnly', () -> @getData()
      @$watch 'dropOnly', () -> @getData()
      @$watch 'rankS', () -> @getData()
      @$watch 'rankA', () -> @getData()
      @$watch 'rankB', () -> @getData()
      @$watch 'rankC', () -> @getData()
      @$watch 'rankD', () -> @getData()
      @$watch 'rankE', () -> @getData()
