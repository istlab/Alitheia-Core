//
// Boost.Process
//
// Copyright (c) 2006 Julio M. Merino Vidal.
//
// Distributed under the Boost Software License, Version 1.0.
// (See accompanying file LICENSE_1_0.txt or copy at
// http://www.boost.org/LICENSE_1_0.txt.)
//

//!
//! \file boost/process/command_line.hpp
//!
//! Includes the declaration of the command_line class.
//!

#if !defined(BOOST_PROCESS_COMMAND_LINE_HPP)
/** \cond */
#define BOOST_PROCESS_COMMAND_LINE_HPP
/** \endcond */

#include <boost/process/config.hpp>

#if defined(BOOST_PROCESS_POSIX_API)
#elif defined(BOOST_PROCESS_WIN32_API)
#   include <tchar.h>
#   include <windows.h>
#   include <boost/process/exceptions.hpp>
#   include <boost/throw_exception.hpp>
#else
#   error "Unsupported platform."
#endif

#include <sstream>
#include <string>
#include <vector>

#include <boost/assert.hpp>
#include <boost/process/detail/command_line_ops.hpp>

namespace boost {
namespace process {

// ------------------------------------------------------------------------

//!
//! \brief Implementation of the Command_Line concept.
//!
//! A command_line object represents the command line used to launch a
//! process.  It includes the path to the executable file as well as all
//! the arguments passed to it.  This class also includes some convenience
//! functions to ease the launcher's task.
//!
//! It is important to note that the list of arguments passed to the binary
//! must always include at least an element that indicates the program's
//! name.  Its value generally matches the binary's base name, but can be
//! changed by the user.  The class enforces this requirement.
//!
class command_line
{
public:
    //!
    //! \brief Type used to represent the argument's vector.
    //!
    typedef std::vector< std::string > arguments_vector;

private:
    //!
    //! \brief Path to the executable program.
    //!
    std::string m_executable;

    //!
    //! \brief List of arguments passed to the executable program.
    //!
    arguments_vector m_arguments;

public:
    //!
    //! \brief Constructs a new command line.
    //!
    //! Constructs a new command line given an executable and its program
    //! name.  If the executable name does not have any directory
    //! component, it is automatically searched in the path given by
    //! \a path or, if empty, in the directories listed by the PATH
    //! environment variable.
    //!
    //! \param executable The full path to the executable program.
    //! \param firstarg The program's name.  If empty, the constructor
    //!                 sets it to be the executable's base name (i.e.,
    //!                 skips the directory).
    //! \param path Set of directories where to look for the executable,
    //!             if it does not include any path component.
    //! \throw not_found_error&lt;std::string&gt; If the executable
    //! cannot be found in the path.
    //!
    //!
    explicit command_line(const std::string& executable,
                          const std::string& firstarg = "",
                          const std::string& path = "");

    //!
    //! \brief Constructs a shell-based command line.
    //!
    //! Constructs a command line that is fed to the system's shell for
    //! further processing.  This command line is subject to pattern
    //! expansion, redirection and pipelining.
    //!
    //! In a POSIX system, the command line is fed to /bin/sh; under a
    //! Win32 system, it is fed to cmd.exe.  This makes it difficult to
    //! write really portable command lines using this pseudo-constructor.
    //!
    static command_line shell(const std::string& command);

    //!
    //! \brief Appends a new argument to the command line.
    //!
    //! This templated method appends a new argument to the command line.
    //! The argument's type has to be allowed on the RHS of a << operator.
    //!
    //! In a POSIX system, arguments given using this function are passed
    //! to the binary as exactly one argument.  They are not subject to
    //! quoting problems.  In a Win32 system, this is unavoidable given
    //! that the process' command line is provided in a single string where
    //! quoting issues may arise.
    //!
    //! \return A reference to the modified command line to allow
    //!         daisy-chaining calls to argument().
    //!
    template< typename T >
    command_line& argument(const T& argument);

    //!
    //! \brief Returns the arguments list.
    //!
    //! Returns the arguments list.
    //!
    const arguments_vector& get_arguments(void) const;

    //!
    //! \brief Returns the executable's path.
    //!
    //! Returns the executable's path.
    //!
    const std::string& get_executable(void) const;
};

// ------------------------------------------------------------------------

inline
command_line::command_line(const std::string& executable,
                           const std::string& firstarg,
                           const std::string& path) :
    m_executable(executable)
{
    if (firstarg.empty()) {
        std::string::size_type pos;

#if defined(BOOST_PROCESS_POSIX_API)
        pos = m_executable.rfind('/');
#elif defined(BOOST_PROCESS_WIN32_API)
        pos = m_executable.rfind('\\');
        if (pos == std::string::npos)
            pos = m_executable.rfind('/');
#endif

        if (pos == std::string::npos)
            m_arguments.push_back(m_executable);
        else
            m_arguments.push_back(m_executable.substr(pos + 1));
    } else
        m_arguments.push_back(firstarg);

#if defined(BOOST_PROCESS_POSIX_API)
    if (m_executable.find('/') == std::string::npos)
        m_executable = detail::find_in_path(m_executable, path);
#elif defined(BOOST_PROCESS_WIN32_API)
    if (m_executable.find('\\') == std::string::npos)
        m_executable = detail::find_in_path(m_executable, path);
#endif
}

// ------------------------------------------------------------------------

inline
command_line
command_line::shell(const std::string& command)
{
#if defined(BOOST_PROCESS_POSIX_API)
    command_line cl("/bin/sh", "sh");
    return cl.argument("-c").argument(command);
#elif defined(BOOST_PROCESS_WIN32_API)
    TCHAR buf[MAX_PATH];
    UINT res = ::GetSystemDirectory(buf, MAX_PATH);
    if (res == 0)
        boost::throw_exception
            (system_error("boost::process::command_line::shell",
                          "GetWindowsDirectory failed", ::GetLastError()));
    BOOST_ASSERT(res < MAX_PATH);

    command_line cl(std::string(buf) + "\\cmd.exe", "cmd");
    return cl.argument("/c").argument(command);
#endif
}

// ------------------------------------------------------------------------

template< typename T >
inline
command_line&
command_line::argument(const T& argument)
{
    std::ostringstream tmp;
    tmp << argument;
    m_arguments.push_back(tmp.str());
    return *this;
}

// ------------------------------------------------------------------------

inline
const command_line::arguments_vector&
command_line::get_arguments(void)
    const
{
    return m_arguments;
}

// ------------------------------------------------------------------------

inline
const std::string&
command_line::get_executable(void)
    const
{
    return m_executable;
}

// ------------------------------------------------------------------------

} // namespace process
} // namespace boost

#endif // !defined(BOOST_PROCESS_COMMAND_LINE_HPP)
