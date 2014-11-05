$(document).ready ->
  baseAdmiral()
  new CommandWatcher([38, 38, 40, 40, 37, 39, 37, 39, 66, 65]).watch ->
    if localStorage.getItem('sound') != 'false'
      audio = (new Audio('/rest/v1/sound/random'))
      audio.volume = 0.3
      audio.play()

baseAdmiral = ->
  storageKey = 'base_select_number'
  vue = new Vue
    el: '#search-admiral'
    data:
      users: []
      admiralName: ""
    methods:
      getNumber: () ->
        $('#base_select option:selected')[0].value
      getUsers: (number, name) ->
        json = if number == "-1"
          $.getJSON("/rest/v1/search_user?q=#{name}")
        else
          $.getJSON("/rest/v1/search_base_user?base=#{number}&q=#{name}")
        json.done (data) -> vue.$set('users', data)
    ready: ->
      number = localStorage.getItem(storageKey) ? @getNumber()
      $('#base_select').val(number)
      @getUsers(number, "")
      @$watch 'admiralName', =>
        @getUsers(@getNumber(), @admiralName)

  $('#base_select').change ->
    number = vue.getNumber()
    localStorage.setItem(storageKey, number)
    vue.getUsers(number, vue.admiralName)

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
