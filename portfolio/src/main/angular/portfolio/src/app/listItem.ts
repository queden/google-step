/** Class that creates list items to be used in work and project components, holds name, description, and image path for corresponding item
 */
export class ListItem {
    constructor(
        public name: string,
        public dates: string,
        public description: string,
        public imgPath: string,
        public link?: string
    ) { }
}