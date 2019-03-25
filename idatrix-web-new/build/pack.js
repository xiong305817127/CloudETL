const tar = require('tar');
const STANDALONE_ETL = process.env.STANDALONE_ETL === 'true';

//是否是打包ETL
console.log(STANDALONE_ETL,"是否是打包ETL");

const packFileName = STANDALONE_ETL ? 'idatrix-web-etl.tar.gz' : `idatrix-web.tar.gz`;

console.log('==============================================');
console.log('Packaging...');

tar.c({
  gzip: true,
  cwd: './dist/',
  file: packFileName,
}, ['./']).then(_ => console.log('Packaging complete!'));
