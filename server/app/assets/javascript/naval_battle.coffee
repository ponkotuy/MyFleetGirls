$(document).ready ->
  userid = $('#userid').val()
  vue = new Vue
    el: '#battle_log'
    data:
      count: 20
      allCount: 0
      page: 0
      data: []
    methods:
      setPage: (page) ->
        @page = page
      getData: (dat) ->
        $.getJSON "/rest/v1/#{userid}/battle_result", dat, (ret) =>
          @data = ret
      timeToStr: (millis) ->
        moment(millis).format('YYYY-MM-DD HH:mm')
      maxPage: -> Math.min(Math.ceil(@allCount / @count), 10)
      pages: -> [0..(@maxPage() - 1)]
    created: ->
      $.getJSON "/rest/v1/#{userid}/battle_result_count", {}, (ret) =>
        @allCount = ret
      @$watch 'page', (page) ->
        @getData({limit: @count, offset: page*@count})
