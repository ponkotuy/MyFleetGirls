$( ->
  pagingVue = (el) -> new Vue
    el: el
    paramAttributes: ['data-url', 'data-count', 'data-pcount']
    data:
      allCount: 0
      page: 0
      data: []
    methods:
      setPage: (page) ->
        @page = page
      getData: (dat) ->
        $.getJSON @url, dat, (ret) =>
          @data = ret
      getCount: ->
        $.getJSON @count, (ret) =>
          @allCount = ret
      timeToStr: (millis) ->
        moment(millis).format('YYYY-MM-DD HH:mm')
      maxPage: -> Math.min(Math.ceil(@allCount / @pageCount()), 10)
      pages: -> [0..(@maxPage() - 1)]
      pageCount: -> parseInt(@pcount)
    watch:
      'page': (page) ->
        @getData({limit: @pageCount(), offset: page*@pageCount()})
    ready: ->
      @getCount()
      @getData({})

  pagingVue('#ship_create')
  pagingVue('#item_create')
  pagingVue('#remodel')
)
