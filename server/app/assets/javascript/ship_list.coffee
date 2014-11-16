$(document).ready ->
  vue = new Vue
    el: '#ship_list'
    data:
      stypes: []
      search: ""
    methods:
      getJSON: ->
        $.getJSON "/rest/v1/book/ships", {q: @search}, (data) =>
          @stypes = data
    created: ->
      @$watch 'search', =>
        @getJSON()
      @getJSON()
