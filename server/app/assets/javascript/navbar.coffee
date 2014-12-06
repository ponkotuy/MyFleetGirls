$(document).ready ->
  now = (localStorage.getItem('sound') != 'false')

  $('#toggle_sound').bootstrapSwitch({state: now})

  $('#toggle_sound').on 'switchChange.bootstrapSwitch', (event, state) ->
    now = state
    localStorage.setItem('sound', state)
