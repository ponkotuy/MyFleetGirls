$(document).ready ->
  url = '/rest/v1/search_user'
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

  baseUser = new Vue
    el: '#base_user'
    data:
      users: []
    methods:
      getUsers: () ->
        number = $('#base_select option:selected')[0].value
        $.getJSON("/rest/v1/search_base_user/#{number}")
        .done (data) -> baseUser.$set('users', data)
    ready: ->
      @getUsers()


  $('#base_select').change ->
    baseUser.getUsers()
