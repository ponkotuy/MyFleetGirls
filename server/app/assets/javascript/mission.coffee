$(document).ready ->
  userid = $('#userid').val()
  pagetCount = 20
  vue = new Vue
    el: '#mission'
    data:
      stage: 'ALL'
      missions: []
    methods:
      getData: ->
        @missions = []
        @setHash()
        dat = {missionId: @missionId(), limit: pagetCount, offset: 0}
        $.getJSON "/rest/v2/user/#{userid}/mission", dat, (ret) =>
          @missions = ret
      missionId: ->
        if @stage == 'ALL' then undefined else parseInt(@stage)
      toDate: (millis) ->
        moment(millis).format('YYYY-MM-DD HH:mm')
      setHash: ->
        obj = stage: @missionId()
        location.hash = toURLParameter(obj)
      restoreHash: ->
        obj = fromURLParameter(location.hash.replace(/^\#/, ''))
        if obj.stage? then @stage = obj.stage
    compiled: ->
      @restoreHash()
      @getData()
    watch:
      'stage': -> @getData()
