$( ->
  pagingVue = (el, dataUrl, countUrl) -> new Vue
    el: el
    data:
      count: 10
      allCount: 0
      page: 0
      data: []
    methods:
      setPage: (page) ->
        @page = page
      getData: (dat) ->
        $.getJSON dataUrl, dat, (ret) =>
          @data = ret
      timeToStr: (millis) ->
        moment(millis).format('YYYY-MM-DD HH:mm')
      maxPage: -> Math.min(Math.ceil(@allCount / @count), 10)
      pages: -> [0..(@maxPage() - 1)]
    created: ->
      $.getJSON countUrl, {}, (ret) =>
        @allCount = ret
      @$watch 'page', (page) ->
        @getData({limit: @count, offset: page*@count})

  userId = $('#userid').val()
  pagingVue('#ship_create', "/rest/v1/#{userId}/create_ships", "/rest/v1/#{userId}/create_ship_count")
  pagingVue('#item_create', "/rest/v1/#{userId}/create_items", "/rest/v1/#{userId}/create_item_count")
)
