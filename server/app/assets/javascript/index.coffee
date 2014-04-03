$(document).ready ->
  url = 'rest/v1/search_user'
  timeout = 0
  search = new Vue
    el: '#search_user'
    data:
      query: ''
      users: []
    methods:
      search: (that, q) ->
        () ->
          $.getJSON url, {q: q}, (ret) =>
            that.users = ret
    created: ->
      @$watch 'query', (q) ->
        if q != ""
          clearTimeout(timeout)
          timeout = setTimeout(@search(this, q), 500)
