#ifndef DATABASE_H
#define DATABASE_H

#include <algorithm>
#include <string>
#include <map>
#include <vector>

#include <boost/bind.hpp>
#include <boost/variant.hpp>

#include "dbobject.h"

namespace CORBA
{
    class Any;
}

namespace Alitheia
{
    /**
     * Database provides access to Alitheia's database.
     */
    class Database
    {
    public:
        Database();
        virtual ~Database();

        /**
         * Adds \a object as record into the database.
         */
        template< class T >
        bool addRecord( T& object )
        {
            CORBA::Any corbaObject = object;
            const bool result = addCorbaRecord( corbaObject );
            object = T::fromCorba( corbaObject );
            return result;
        }

        bool deleteRecord( const DAObject& object );
        
        /**
         * Updates the record of \a object within the database.
         */
        template< class T >
        bool updateRecord( T& object )
        {
            CORBA::Any corbaObject = object;
            const bool result = updateCorbaRecord( corbaObject );
            object = T::fromCorba( corbaObject );
            return result;
        }
        
        /**
         * Finds an object of type T having \a id within the database.
         */
        template< class T >
        T findObjectById( int id ) const
        {
            return T::fromCorba( *findObjectById( T(), id ) );
        }

        typedef std::string property_map_key;
        typedef boost::variant< int,
                                bool,
                                std::string,
                                Developer,
                                Directory,
                                FileGroup,
                                Metric,
                                MetricType,
                                Plugin,
                                PluginConfiguration,
                                ProjectFile,
                                ProjectFileMeasurement,
                                ProjectVersion,
                                ProjectVersionMeasurement,
                                StoredProject > property_map_value;
        typedef std::map< property_map_key, property_map_value > property_map;
        typedef property_map_value db_row_entry;

        /**
         * Gets a list of objects of type T matching a set of properties.
         * The properties can be everything like "name", "project".
         */
        template< class T >
        std::vector< T > findObjectsByProperties( const property_map& properties ) const
        {
            using namespace boost;
            const std::vector< CORBA::Any > objects = findObjectsByProperties( T(), properties );
            std::vector< T > result;
            result.resize( objects.size() );
            std::transform( objects.begin(), 
                            objects.end(), 
                            result.begin(), 
                            bind( &T::fromCorba, _1 ) );
            return result;
        }

        std::vector< db_row_entry > doHQL( const std::string& hql, const property_map& params = property_map() );
        std::vector< db_row_entry > doSQL( const std::string& sql, const property_map& params = property_map() );

    private:
        CORBA::Any* findObjectById( const CORBA::Any& type, int id ) const;
        std::vector< CORBA::Any > findObjectsByProperties( const CORBA::Any& type, const property_map& ) const;
        bool addCorbaRecord( CORBA::Any& record );
        bool updateCorbaRecord( CORBA::Any& record );

        class Private;
        Private* const d;
    };
}

#endif
