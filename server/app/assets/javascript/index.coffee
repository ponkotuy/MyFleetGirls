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

  new CommandWatcher([38, 38, 40, 40, 37, 39, 37, 39, 66, 65]).watch ->
    if localStorage.getItem('sound') != 'false'
      audio = (new Audio('/rest/v1/sound/random'))
      audio.volume = 0.3
      audio.play()

class CommandWatcher
  constructor: (commands) ->
    @keys = []
    @length = commands.length
    @command = commands.join ','
  watch: (handler) =>
    watcher = @
    $(document).on 'keydown', (event) ->
      watcher.keys.push event.which
      # マッチしたら実行後、即return
      if watcher.keys.length is watcher.length and watcher.keys.join(',') is watcher.command
        handler()
        watcher.keys = []
        return
      # マッチしなかったらリセット
      if watcher.command.indexOf(watcher.keys.join(',')) isnt 0
        watcher.keys = []
        return
