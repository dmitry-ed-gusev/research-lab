import moment from 'moment';
import jQuery from 'jquery';
import popper from 'popper.js';
import bootstrap from 'bootstrap';
import _ from 'lodash';

function component() {
    const element = document.createElement('div');

    // Lodash, now imported by this script
    element.innerHTML = _.join(['Hello', 'webpack'], ' ');

    return element;
}

console.log("Hello from JavaScript!");
console.log(moment().startOf('day').fromNow());

var name = "Bob", time = "today";
console.log(`Hello ${name}, how are you ${time}?`);

jQuery(function() {
    jQuery('body').css('color', 'blue');
});

// bootstrap's command
document.body.style.color = "blue";

// call component() function
document.body.appendChild(component());
