// helper to initialize one slider
function initRangeSlider(containerSelector, minName, maxName, options) {
    document.querySelectorAll(containerSelector).forEach(sliderEl => {
      // find the form that contains this slider
      const form = sliderEl.closest('form');
      const minInput = form.querySelector(`input[name="${minName}"]`);
      const maxInput = form.querySelector(`input[name="${maxName}"]`);
  
      // parse existing values or fallback
      const startMin = parseFloat(minInput.value) || options.range.min;
      const startMax = parseFloat(maxInput.value) || options.range.max;
  
      // create the noUiSlider
      noUiSlider.create(sliderEl, {
        start: [ startMin, startMax ],
        connect: true,
        range: options.range,
        step: options.step,
        tooltips: [ true, true ],
        format: options.format
      });
  
      // update inputs on slide
      sliderEl.noUiSlider.on('update', (values, handle) => {
        if (handle === 0) minInput.value = values[0];
        else              maxInput.value = values[1];
      });
    });
  }
  
  // common formatters
const intFormatter = {
to: v => Math.round(v),
from: v => Number(v)
};
const twoDecimalFormatter = {
to: v => v.toFixed(2),
from: v => Number(v)
};

// initialize your three sliders (both desktop & phone)
initRangeSlider('.price-slider',  'minPrice',  'maxPrice', {
range: { min: 0,    max: 30000 },
step:  250,
format: intFormatter
});

initRangeSlider('.width-slider',  'minWidth',  'maxWidth', {
range: { min: 0,    max: 200 },
step:  1,
format: twoDecimalFormatter
});

initRangeSlider('.height-slider', 'minHeight', 'maxHeight', {
range: { min: 0,    max: 200 },
step:  1,
format: twoDecimalFormatter
});
  