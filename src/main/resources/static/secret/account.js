const j_error_panel = $('#error-panel');
const j_error_message = $('#error-message');

const j_success_panel = $('#success-panel');
const j_success_message = $('#success-message');

const j_current_pw = $('#current_password');
const j_new_pw = $('#password');
const j_new_pwc = $('#confirm_password');

const j_groups = $('#level_groups');
const j_level = $('#custom_level');

const j_tokens = $('#tokens');

const j_enable_toggle = $('#enable-toggle');
const j_deactivate = $('#deactivate');

let loop_guard = false;

function onPasswordChangeSubmit(self) {
  const current_pw = j_current_pw.val();
  const new_pw = j_new_pw.val();
  const new_pwc = j_new_pwc.val();

  if (new_pw !== new_pwc) {
    setErrorText('Passwords do not match');
  } else {
    _ajax({
      type: 'POST',
      url: `/user/set/password/${username}`,
      data: {
        verificationPassword: current_pw,
        password: new_pw
      },
      success: function (data) {
        setSuccessText('Users password successfully updated!');

        j_current_pw.val('');
        j_new_pw.val('');
        j_new_pwc.val('');
      },
      error: errorHandler
    });
  }
}

function onLevelSubmit(self) {
  const current_pw = j_current_pw.val();
  const level = parseInt(j_level.val());

  _ajax({
    type: 'POST',
    url: `/user/set/level/${username}`,
    data: {
      verificationPassword: current_pw,
      level: level
    },
    success: function (data) {
      setSuccessText('Users level successfully updated!');
    },
    error: errorHandler
  });
}

function onTokenRevokeSubmit(self) {
  const current_pw = j_current_pw.val();
  const selected = j_tokens.children('option:selected').map((i, v, t) => $(v).val()).toArray();

  if (selected.length < 1) {
    setErrorText('No tokens selected');
    return;
  }

  _ajax({
    type: 'POST',
    url: `/user/tokens/revoke/${username}`,
    data: {
      verificationPassword: current_pw,
      uuids: selected
    },
    success: function (data) {
      setSuccessText('Successfully revoked token(s)!');
      j_tokens.children('option').each((i, v) => {
        const o = $(v);
        if (selected.includes(o.val())) {
          o.remove();
        }
      });
    },
    error: errorHandler
  });
}

function onEnableToggle(self) {
  const $this = $(self);
  const current_pw = j_current_pw.val();

  _ajax({
    type: 'POST',
    url: `/user/set/enabled/${username}`,
    data: {
      verificationPassword: current_pw,
      enabled: $this.val() > 0
    },
    success: function (data) {
      const enabled = parseInt(data) > 0;

      setSuccessText(`User ${enabled ? 'Enabled' : 'Disabled'}`);

      $this.val(enabled ? 0 : 1);
      $this.text(enabled ? 'Disable' : 'Enable');
      $this.removeClass(enabled ? 'btn-outline-success' : 'btn-outline-warning')
      .addClass(enabled ? 'btn-outline-warning' : 'btn-outline-success');
    },
    error: errorHandler
  });
}

function onDeactivate(self) {
  const current_pw = j_current_pw.val();

  if (typeof j_deactivate.attr('sure') === 'undefined') {
    j_deactivate.text('Are you sure?');
    j_deactivate.attr('sure', 'true');
    return;
  }

  j_deactivate.text('Deactivate');
  j_deactivate.removeAttr('sure');

  _ajax({
    type: 'POST',
    url: `/user/unregister/${username}`,
    data: {
      verificationPassword: current_pw
    },
    success: function (data) {
      $(location).attr('href', '/accounts?success');
    },
    error: errorHandler
  });
}

function setErrorText(text, internal) {
  if (typeof internal === 'undefined') {
    setSuccessText('', true);
  }
  if (typeof text !== 'undefined' && text !== '') {
    j_error_panel.collapse('show');
    j_error_message.text(text);
  } else {
    j_error_panel.collapse('hide');
    j_error_message.text('');
  }
}

function setSuccessText(text, internal) {
  if (typeof internal === 'undefined') {
    setErrorText('', true);
  }
  if (typeof text !== 'undefined' && text !== '') {
    j_success_panel.collapse('show');
    j_success_message.text(text);
  } else {
    j_success_panel.collapse('hide');
    j_success_message.text('');
  }
}

async function _ajax(data, errorHandler) {
  try {
    return await $.ajax(data);
  } catch (e) {
    if (typeof errorHandler !== 'undefined') {
      errorHandler(e);
    }
  }
}

function errorHandler(data) {
  if (typeof data !== 'object' || typeof data.responseJSON !== 'object') {
    setErrorText('Unknown server response');
  } else {
    const json = data.responseJSON;
    if (data.status === 406) {
      setErrorText(json.message);
    } else {
      if (typeof json.message === 'undefined' || json.message === '') {
        setErrorText(json.error);
      } else {
        setErrorText(`${json.error}: ${json.message}`);
      }
    }
  }
}

$(function () {
  if (typeof username === 'undefined' || username === '') {
    setErrorText('Username field is not defined');
  }

  j_groups.change(function (self) {
    if (!loop_guard) {
      const selected = j_groups.children('option:selected');
      if (typeof selected !== 'undefined') {
        const val = parseInt(selected.attr('value'));
        loop_guard = true;
        try {
          j_level.val(val);
        } finally {
          loop_guard = false;
        }
      }
    }
  });

  j_level.change(function (self) {
    if (!loop_guard) {
      loop_guard = true;
      try {
        j_groups.val(parseInt(j_level.val()));
      } finally {
        loop_guard = false;
      }
    }
  });

  j_deactivate.mouseleave(function () {
    if (typeof j_deactivate.attr('sure') !== 'undefined') {
      j_deactivate.text('Deactivate');
      j_deactivate.removeAttr('sure');
    }
  });

  $("[id*='collapse_']").each(function () {
    const o = $(this);
    const view = o.attr('data');

    o.on('show.bs.collapse', function () {
      const url = new URL(location.href);
      url.searchParams.set('edit', view);
      window.history.pushState({}, null, url.href);
    });

    o.on('hide.bs.collapse', function () {
      const url = new URL(location.href);
      if (url.searchParams.get('edit') === view) {
        url.searchParams.delete('edit');
        window.history.pushState({}, null, url.href);
      }
    });
  });
});
