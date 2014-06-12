$(document).ready ->
  userid = $('#userid').val()
  vue = new Vue
    el: "#quest"
    data:
      daily_quests: []
      weekly_quests: []
      once_quests: []
    methods:
      progressView: (state, progress) ->
        switch state
          when 1 then '未受注'
          when 3 then '達成'
          else
            switch progress
              when 1 then '50%以上達成'
              when 2 then '80%以上達成'
              else '受注'
      catView: (cat) ->
        [undefined, '編成', '出撃', '演習', '遠征', '補給/入渠', '工廠', '改装'][cat]
      getJson: () ->
        $.getJSON "/rest/v1/#{userid}/quest", {}, (data) =>
          @daily_quests = data.filter (x) -> [2, 4, 5].indexOf(x.typ) >= 0
          @weekly_quests = data.filter (x) -> x.typ == 3
          @once_quests = data.filter (x) -> [1, 6].indexOf(x.typ) >= 0
      setTooltip: () ->
        $('tr.tltip').tooltip({html: true})
    created: () ->
      @getJson()
    ready: () ->
      @$watch 'once_quests', @setTooltip
