$( ->
  userId = $('#userid').val()

  shipCreateVue = new Vue
    el: '#ship_create'
    data:
      count: 10
      allCount: 0
      page: 0
      ships: []
    methods:
      setPage: (page) ->
        @page = page
      getShips: (dat, f) ->
        $.getJSON "/rest/v1/#{userId}/create_ships", dat, (ret) =>
          @ships = ret
      timeToStr: (millis) ->
        moment(millis).format('YYYY-MM-DD HH:mm')
      maxPage: -> Math.ceil(@allCount / @count)
      pages: -> [0..(@maxPage() - 1)]
    created: ->
      $.getJSON "/rest/v1/#{userId}/create_ship_count", {}, (ret) =>
        @allCount = ret
      @$watch 'page', (page) ->
        @getShips({limit: @count, offset: page*@count})
)
