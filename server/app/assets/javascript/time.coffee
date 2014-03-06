
$(document).ready ->
  moment.lang('ja')

  changeTime = ->
    $('.viewTimeBefore').each () ->
      time = parseInt($(this).attr('data-time'))
      end_mes = $(this).attr('data-end-mes')
      diff = moment(time).diff(moment(), 'minutes')
      text = if diff > 0 then "あと#{diff}分" else end_mes
      $(this).text(text)

  changeTime()
  timer = setInterval(changeTime, 5000)

  $('.viewTime').each () ->
    millis = parseInt($(this).attr('data-time'))
    text = moment(millis).format('YYYY-MM-DD HH:mm')
    $(this).text(text)
