$(document).ready ->
  host = location.host
  userId = $('#userid').val()
  connection = new WebSocket("ws://#{host}/ws/v1/user/#{userId}")

  messages = new Vue
    el: '#messages'
    data:
      messages: []
    methods:
      onNdock: (data) ->
        data.forEach (elem) =>
          if @checkTime(elem.completeTime)
            @messages.unshift({title: '修復完了', mes: "DockID #{elem.id} の#{elem.name}の修復が完了しました"})
            @sound(elem.masterShipId, 27)
      onKdock: (data) ->
        data.forEach (elem) =>
          if @checkTime(elem.completeTime)
            @messages.unshift({title: '建造完了', mes: "DockID #{elem.id} の#{elem.name}の建造が完了しました"})
            @sound(elem.shipId, 1)
      onMission: (data) ->
        data.forEach (elem) =>
          if @checkTime(elem.completeTime)
            @messages.unshift({title: '艦隊帰投', mes: "#{elem.deckName}が#{elem.missionName}から帰投しました"})
            @sound(elem.flagshipId, 7)
      checkTime: (time) ->
        now = moment().valueOf()
        now <= time && time < (now + 60*1000)
      sound: (shipId, soundId) ->
        if localStorage.getItem('sound') != 'false'
          audio = (new Audio("/rest/v1/sound/ship/#{shipId}/#{soundId}.mp3"))
          audio.volume = 0.3
          audio.play()
    created: ->
      connection.onmessage = (e) =>
        data = JSON.parse(e.data)
        @onNdock(data.ndocks)
        @onKdock(data.kdocks)
        @onMission(data.missions)
