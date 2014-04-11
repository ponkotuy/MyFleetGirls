$(document).ready ->
  host = location.host
  userId = $('#userid').val()
  connection = new WebSocket("ws://#{host}/ws/v1/user/#{userId}")

  messages = new Vue
    el: '#messages'
    data:
      messages: []
    methods:
      onKdock: (data) ->
        data.forEach (elem) ->
          if @checkTime(elem.completeTime)
            @messages.push({title: '修復完了', mes: "DockID #{elem.id} の#{elem.name}の修復が完了しました"})
            @sound(elem.shipId, 27)
      onNdock: (data) ->
        data.forEach (elem) ->
          if @checkTime(elem.completeTime)
            @messages.push({title: '建造完了', mes: "DockID #{elem.id} の#{elem.name}の建造が完了しました"})
            @sound(elem.shipId, 5)
      onMission: (data) ->
        data.forEach (elem) ->
          if @checkTime(elem.completeTime)
            @messages.push({title: '艦隊帰投', mes: "#{elem.deckName}が#{elem.missionName}から帰投しました"})
            @sound(elem.flagshipId, 7)
      checkTime: (time) ->
        now = moment().valueOf()
        now <= time && time < (now + 60*1000)
      sound: (shipId, soundId) ->
        (new Audio("/rest/v1/sound/ship/#{shipId}/#{soundId}.mp3")).play()
    created: ->
      connection.onmessage = (e) =>
        @messages = []
        @onKdock(e.data.kdocks)
        @onNdock(e.data.ndocks)
        @onMission(e.data.missions)
