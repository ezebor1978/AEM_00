/**
 * Extension to the standard dropdown/select and checkbox component. It enabled hidding/unhidding of multiple dialog fields based on the
 * selection made in the dropdown/select or on checkbox check.
 * USE ONLY WITH coral3 multifield item
 *
 * How to use:
 * 1. Select Field
 * - add the empty property cq-dialog-dropdown-showhide to the dropdown/select element
 * - add the data attribute cq-dialog-showhide-target to the dropdown/select element, value should be the
 *   selector, usually a specific class name, to find all possible target elements that can be shown/hidden.
 * - add the target class to each target component that can be shown/hidden
 * - add the class hidden to each target component to make them initially hidden
 * - add the attribute showhidetargetvalue to each target component, the value should equal the value of the select
 *   option that will unhide this element.
 *
 * 2. Checkbox Field
 * - add the empty property cq-dialog-checkbox-showhide to the checkbox element
 * - add the data attribute cq-dialog-showhide-target to the checkbox element, value should be the
 *   selector, usually a specific class name, to find all possible target elements that can be shown/hidden.
 * - add the target class to each target component that can be shown/hidden
 * - add the class hidden to each target component to make them initially hidden
 * - add the attribute showhidetargetvalue to each target component, the value should equal to:
 * 	 'true', if the field is to be displayed when Checkbox is selected.
 * 	 'false', if the field is to be displayed when Checkbox is unselected.
 *
 * 	  * 3. MultiFields
 * - add the data attribute cq-dialog-multifield-target to the element, value should match the
 *   name of fieldset element right under the multifield element.
 * - add the attribute cq-dialog-multifield-target-value to the element, the value should equal to the value matching
 *   in the select option or if a checkbox:
 * 	 'true', if the multifield is to be displayed when Checkbox is selected.
 * 	 'false', if the multifield is to be displayed when Checkbox is unselected.
 */

(function(document, $) {
    'use strict';

    // when dialog gets injected
    $(document).on('foundation-contentloaded', function(e) {
        // if there is already an inital value make sure the according target element becomes visible
        showHide(
            $('[data-cq-dialog-dropdown-showhide], [data-cq-dialog-checkbox-showhide]', e.target)
        );
    });

    $(document).on('change', '[data-cq-dialog-dropdown-showhide]', function(e) {
        showHide($(this));
    });

    $(document).on('change', '[data-cq-dialog-checkbox-showhide]', function(e) {
        showHide($(this));
    });

    function showHide(el) {
        el.each(function(i, element) {
            Coral.commons.ready(element, function (component) {
                var target, value, multifieldTarget, multifieldValue;
                var type = getFieldType(component);

                switch (type) {
                    case 'select':
                        value = component.value;
                        break;
                    case 'checkbox':
                        value = $(component).prop('checked');
                        break;
                }

                // get the selector to find the target elements. its stored as data-.. attribute
                target = $(component).data('cq-dialog-showhide-target');

                // get the multifield selector to find the multifield element to hide/show
                multifieldTarget = $(component).data('cq-dialog-multifield-target');

                if (target) {
                    hideUnselectedElements(component, target, value);
                    showTarget(component, target, value);
                }
                if (multifieldTarget) {
                    // get the multifield target value to define when to hide/show the multifield target
                    multifieldValue = $(component).data('cq-dialog-multifield-target-value');
                    showMultifieldTarget(component, multifieldTarget, value !== multifieldValue);
                }
            });
        });
    }

    //Get type of field
    function getFieldType(element) {
        //Check if field is a checkbox
        var type = $(element).prop('type');
        if (type === 'checkbox') {
            return 'checkbox';
        }
        //Check if field is a dropdown
        var select = $(element).hasClass('coral3-Select');
        if (select) {
            return 'select';
        }

        //Check if field is a CoralUI3 checkbox
        if (element && element.tagName === 'CORAL-CHECKBOX') {
            return 'checkbox';
        }
    }

    // make sure all unselected target elements are hidden.
    function hideUnselectedElements(element, target, value) {
        var multifieldItemParent = $(element).parentsUntil('', 'coral-multifield-item');
        var filterIn = multifieldItemParent.length ? multifieldItemParent.find(target) : $(target);
        filterIn.not('.hide').each(function() {
            $(this).addClass('hide'); //If target is a container, hides the container
            $(this)
                .closest('.coral-Form-fieldwrapper')
                .addClass('hide'); // Hides the target field wrapper. Thus, hiding label, quicktip etc.
        });
    }

    // unhide the target element that contains the selected value as data-showhidetargetvalue attribute
    function showTarget(element, target, value) {
        var multifieldItemParent = $(element).parentsUntil('', 'coral-multifield-item');
        var filterIn = multifieldItemParent.length ? multifieldItemParent.find(target) : $(target);
        filterIn.filter("[data-showhidetargetvalue*='" + value + "']").each(function() {
            $(this).removeClass('hide'); //If target is a container, unhides the container
            $(this)
                .closest('.coral-Form-fieldwrapper')
                .removeClass('hide'); // Unhides the target field wrapper. Thus, displaying label, quicktip etc.
        });
    }

    // toggle target Multifield element depending on showMultifield flag
    function showMultifieldTarget(element, multifieldName, showMultifield) {
        var selector = "[data-granite-coral-multifield-name='" + multifieldName + "']";
        var multifieldItemParent = $(element).parentsUntil('', 'coral-multifield-item');
        var multiFieldTarget = multifieldItemParent.length
            ? multifieldItemParent.find(selector)
            : $(selector);

        multiFieldTarget.toggleClass('hide', showMultifield);
        multiFieldTarget.closest('.coral-Form-fieldwrapper').toggleClass('hide', showMultifield);
    }
})(document, Granite.$);
