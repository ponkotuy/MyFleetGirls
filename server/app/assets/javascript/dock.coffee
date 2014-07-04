$(document).ready ->
  host = location.host
  userId = $('#userid').val()

  messages = new Vue
    el: '#messages'
    data:
      messages: []
      ndocks: []
      kdocks: []
      missions: []
      conds: []
    methods:
      onNdock: (data) ->
        @ndocks = data
        data.forEach (elem) =>
          if @checkTime(elem.completeTime)
            @messages.unshift({title: '修復完了', mes: "DockID #{elem.id} の#{elem.name}の修復が完了しました"})
            @sound(elem.masterShipId, 26)
      onKdock: (data) ->
        @kdocks = data
        data.forEach (elem) =>
          if @checkTime(elem.completeTime)
            @messages.unshift({title: '建造完了', mes: "DockID #{elem.id} の#{elem.name}の建造が完了しました"})
            @sound(elem.shipId, 1)
      onMission: (data) ->
        @missions = data
        data.forEach (elem) =>
          if @checkTime(elem.completeTime)
            @messages.unshift({title: '艦隊帰投', mes: "#{elem.deckName}が#{elem.missionName}から帰投しました"})
            @sound(elem.flagshipId, 7)
      onCond: (data) ->
        @conds = data.sort (a, b) -> a.cond - b.cond

      checkTime: (time) ->
        now = moment().valueOf()
        now <= time && time < (now + 60*1000)
      sound: (shipId, soundId) ->
        if localStorage.getItem('sound') != 'false'
          audio = (new Audio("/rest/v1/sound/ship/#{shipId}/#{soundId}.mp3"))
          audio.volume = 0.3
          audio.play()
      timeView: (time, endMessage) ->
        diff = moment(time).diff(moment(), 'minutes')
        if diff > 0
          "あと#{diff}分"
        else if diff <= 0
          endMessage
        else ""

      checkdata: (that) ->
        () ->
          $.getJSON "/rest/v1/#{userId}/ndocks", (data) ->
            that.onNdock(data)
          $.getJSON "/rest/v1/#{userId}/kdocks", (data) ->
            that.onKdock(data)
          $.getJSON "/rest/v1/#{userId}/missions", (data) ->
            that.onMission(data)
          $.getJSON "/rest/v1/#{userId}/conds", (data) ->
            that.onCond(data)
    created: ->
      @checkdata(this)()
      setInterval(@checkdata(this), 60*1000)
