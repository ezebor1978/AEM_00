$(window)
    .adaptTo('foundation-registry')
    .register('foundation.validation.validator', {
        selector:
            '[data-cq-dialog-input-required-not-hidden], [data-cq-dialog-input-required-not-hidden] span input',
        validate: function(el) {
            let wrapper = $(el).closest('.type-showhide')[0];
            if (!el.value.trim() && (wrapper && !wrapper.classList.contains('hide'))) {
                return 'Please fill out this field';
            }
        }
    });


