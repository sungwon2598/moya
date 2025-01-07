import React from 'react';

const Footer: React.FC = () => {
    return (
        <footer className="bdT ta-c p-30 lh-0 fsz-sm c-grey-600">
      <span>
        Copyright © {new Date().getFullYear()} Designed by
          {' '}
          <a
              href="https://colorlib.com"
              target="_blank"
              rel="nofollow noopener noreferrer"
              title="Colorlib"
          >
          Colorlib
        </a>. All rights reserved.
      </span>
        </footer>
    );
};

export default Footer;
