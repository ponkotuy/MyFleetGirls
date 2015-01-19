$(document).ready ->
  userid = $('#userid').val()
  pageCount = 20
  vue = new Vue
    el: '#mission'
    data:
      stage: 'ALL'
      missions: []
      userid: userid
      page: 0
      allCount: 0
      pageCount: pageCount
    methods:
      getData: ->
        @missions = []
        @setHash()
        dat = {missionId: @missionId(), limit: pageCount + 1, offset: @page * 20}
        $.getJSON "/rest/v2/user/#{userid}/mission_count", dat, (ret) =>
          @allCount = ret
        $.getJSON "/rest/v2/user/#{userid}/mission", dat, (ret) =>
          @hasNext = ret.length > pageCount
          @missions = ret.slice(0, pageCount)
      missionId: ->
        if @stage == 'ALL' then undefined else parseInt(@stage)
      toDate: (millis) ->
        moment(millis).format('YYYY-MM-DD HH:mm')
      setHash: ->
        obj = {stage: @missionId(), page: @page}
        location.hash = toURLParameter(obj)
      restoreHash: ->
        obj = fromURLParameter(location.hash.replace(/^\#/, ''))
        @stage = obj.stage ? @stage
        @page = obj.page ? @page
      setPage: (page) -> @page = page
      maxPage: () -> Math.min(Math.ceil(@allCount / pageCount), 10)
      pages: () -> [0..(@maxPage() - 1)]
    compiled: ->
      @restoreHash()
      @getData()
    watch:
      'stage': ->
        if @page == 0
          @getData()
        else
          @setPage(0)
      'page': -> @getData()
