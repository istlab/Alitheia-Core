#ifndef FDS_H
#define FDS_H

#include <string>
#include <vector>

#include "dbobject.h"

namespace Alitheia
{
    class Checkout;
    
    /**
     * The FDS (Fat Data Service) is the service to retrieve the contents of files and projects.
     */
    class FDS
    {

    public:
        FDS();
        virtual ~FDS();

        std::string getFileContents( const ProjectFile& file ) const;
        std::string getFileContents( const ProjectFile& file, int begin, int length ) const;

        Checkout getCheckout( const ProjectVersion& version, const std::string& pattern = std::string() ) const;

    private:
        class Private;
        Private* d;
    };

    /**
     * Checkout is a checkout of a project version. I.e. it contains references to all
     * project files contained in the version.
     * Use the FDS class to retriev a Checkout.
     */
    class Checkout
    {
        friend class ::Alitheia::FDS;
    protected:
        /**
         * Creates a new Checkout.
         */
        Checkout() {}
        
    public:
        explicit Checkout( const eu::sqooss::impl::service::corba::alitheia::Checkout& checkout );
        virtual ~Checkout();

        ProjectVersion version;
        std::vector< ProjectFile > files;

        void saveFile( const std::string& directory, const ProjectFile& file ) const;
        void save( const std::string& directory ) const;
    };
}

#endif
