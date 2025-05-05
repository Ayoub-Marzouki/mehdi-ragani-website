const secondaryImages = document.getElementById('secondary-images');
const addSecondaryImageButton = document.getElementById('add-secondary-images');

let index = 1;
addSecondaryImageButton.addEventListener("click", () => {
    secondaryImages.innerHTML+= `
    <div class="field">
        <label for="secondaryImage${index}">Secondary Image Path:</label>
        <input type="text" name="secondaryImagesPaths[${index}]" id="secondaryImage${index}" placeholder="/images/artworks/…"/>
    </div>
    `;
    index++;
});