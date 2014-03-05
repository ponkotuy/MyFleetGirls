
$(document).ready ->
  moment.lang('ja')

  changeTime = ->
    $('.viewTime').each () ->
      time = parseInt($(this).attr('data-time'))
      console.log(time)
      end_mes = $(this).attr('data-end-mes')
      diff = moment(time).diff(moment(), 'minutes')
      text = if diff > 0 then "あと#{diff}分" else end_mes
      $(this).text(text)

  changeTime()
  timer = setInterval(changeTime, 5000)
